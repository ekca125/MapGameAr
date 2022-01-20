package com.ekcapaper.racingar.data;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;

public class NakamaNetworkManager {
    // info
    @Getter
    private final Client client;
    @Getter
    private final SocketClient socketClient;
    @Getter
    private Session session;

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

    // group
    GroupList getAllGroupListSync() throws ExecutionException, InterruptedException {
        return client.listGroups(session, "%", 100).get();
    }

    Group createGroupSync(String name, String desc) throws ExecutionException, InterruptedException {
        return client.createGroup(session, name, desc, null, null, true).get();
    }

    Group joinGroupSync(String groupId) throws ExecutionException, InterruptedException {
        client.joinGroup(session, groupId).get();
        return getAllGroupListSync().getGroupsList().stream()
                .filter(group -> group.getId().equals(groupId))
                .collect(Collectors.toList())
                .get(0);
    }

    void deleteGroupSync(String groupId) throws ExecutionException, InterruptedException {
        client.deleteGroup(session, groupId).get();
    }

    void leaveGroupSync(String groupId) {
        try {
            client.leaveGroup(session, groupId).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }
    //

    // match
    Match createMatchSync(SocketListener socketListener) throws ExecutionException, InterruptedException {
        socketClient.connect(session, socketListener);
        return socketClient.createMatch().get();
    }


    Match joinMatchSync(SocketListener socketListener, String matchId) throws ExecutionException, InterruptedException {
        socketClient.connect(session, socketListener);
        return socketClient.joinMatch(matchId).get();
    }


    void leaveMatchSync(String matchId) {
        try {
            socketClient.leaveMatch(matchId).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }
    //

}