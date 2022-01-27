package com.ekcapaper.racingar.nakama;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.GameMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.Rpc;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjectAcks;
import com.heroiclabs.nakama.api.StorageObjects;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NakamaNetworkManager {
    private final Client client;
    private final SocketClient socketClient;
    Session session;

    private Gson gson;

    public NakamaNetworkManager() {
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        session = null;
        //
        gson = new Gson();
    }

    // session
    public boolean isLogin() {
        return session != null;
    }

    public boolean loginEmailSync(String email, String password) {
        try {
            session = client.authenticateEmail(email, password).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            session = null;
            return false;
        }
    }

    public void logout() {
        if (session != null) {
            session = null;
        }
    }

    // session info
    public String getCurrentSessionUserId() {
        return session.getUserId();
    }
    //

    // group
    public GroupList getAllGroupList() {
        try {
            return client.listGroups(session, "%", 99).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public GroupList getGroupList(String groupFilter) {
        try {
            return client.listGroups(session, groupFilter, 99).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private Group findGroup(String groupName) {
        try {
            return client.listGroups(session, groupName, 99).get().getGroupsList().get(0);
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public GroupUserList getGroupUserList(String groupName) {
        Group group = findGroup(groupName);
        if (group == null) {
            return null;
        }
        try {
            return client.listGroupUsers(session, group.getId()).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public GroupUserList getGroupUserList(@NonNull Group group) {
        try {
            return client.listGroupUsers(session, group.getId()).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public Group createGroupSync(String groupName, String groupDesc) {
        try {
            return client.createGroup(session, groupName, groupDesc, null, null, true).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public Group joinGroupSync(String groupName) {
        try {
            Group group = findGroup(groupName);
            if (group == null) {
                return null;
            }
            client.joinGroup(session, group.getId()).get();
            return group;
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private void deleteGroupSync(String groupName) {
        try {
            Group group = findGroup(groupName);
            if (group == null) {
                return;
            }
            client.deleteGroup(session, group.getId()).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }

    public void leaveGroupSync(String groupName) {
        Group group = findGroup(groupName);
        if (group == null) {
            return;
        }
        try {
            if (group.getCreatorId().equals(session.getUserId())) {
                deleteGroupSync(groupName);
            }
            client.leaveGroup(session, group.getId()).get();
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException ignored) {
        }
    }
    //

    // rpc (client)
    public JsonObject clientRpcSync(String rpcFunctionName, String rpcFunctionPayload) throws ExecutionException, InterruptedException{
        Rpc rpcResult = client.rpc(session,rpcFunctionName,rpcFunctionPayload).get();
        return gson.fromJson(rpcResult.getPayload(),JsonObject.class);
    }
    //

    // match
    public Match createMatchSync(SocketListener socketListener, String label) {
        try {
            String rpcFunctionName = "create_match_racingar";
            Map<String,String> payload = new HashMap<>();
            payload.put("label",label);

            JsonObject jsonObject = clientRpcSync(rpcFunctionName,gson.toJson(payload));
            String matchId = jsonObject.get("matchid").toString().replace("\"","");
            return joinMatchSync(socketListener ,matchId);
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public Match joinMatchSync(SocketListener socketListener, String matchId) {
        try {
            socketClient.connect(session, socketListener);
            return socketClient.joinMatch(matchId).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public void sendMatchData(String matchId, GameMessage gameMessage) {
        socketClient.sendMatchData(
                matchId,
                gameMessage.getOpCode().ordinal(),
                gameMessage.getPayload().getBytes(StandardCharsets.UTF_8)
        );
    }

    public void leaveMatchSync(String matchId) {
        try {
            socketClient.leaveMatch(matchId).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }
    //


    // collection - key - userId로 저장된다.
    public boolean writePublicServerStorageSync(String collectionName, String keyName, Map<String, Object> data) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);
        try {
            StorageObjectWrite saveObject = new StorageObjectWrite(
                    collectionName,
                    keyName,
                    jsonData,
                    PermissionRead.PUBLIC_READ,
                    PermissionWrite.OWNER_WRITE);
            StorageObjectAcks acks = client.writeStorageObjects(session, saveObject).get();
            return true;
        } catch (ExecutionException | InterruptedException ignored) {
            return false;
        }
    }

    public Map<String, Object> readServerStorageSync(String collectionName, String keyName, String storageOwnUserId) {
        Gson gson = new Gson();
        try {
            StorageObjectId objectId = new StorageObjectId(collectionName);
            objectId.setKey(keyName);
            objectId.setUserId(storageOwnUserId);
            StorageObjects objects = client.readStorageObjects(session, objectId).get();
            StorageObject object = objects.getObjects(0);
            return gson.fromJson(object.getValue(), Map.class);
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            return null;
        }
    }


}