package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.Rpc;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Response;

public class ThisApplication extends Application {
    // info
    @Getter
    private Client client;
    @Getter
    private SocketClient socketClient;
    @Getter
    private Session session;

    // group, match
    @Getter
    private Group currentGameRoomGroup;
    @Getter
    private Match currentGameRoomMatch;
    @Getter
    private GameRoomPlayOperator currentGameRoomOperator;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // info
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
        // group, match
        currentGameRoomGroup = null;
        currentGameRoomMatch = null;
        currentGameRoomOperator = null;
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
    Group createGroupSync(String name, String desc) throws ExecutionException, InterruptedException {
        return client.createGroup(session, name, desc, null, null, true).get();
    }

    public boolean createGameRoomGroupSync(String name, String desc) {
        if (currentGameRoomGroup != null) {
            throw new IllegalStateException();
        }
        try {
            currentGameRoomGroup = createGroupSync(name, desc);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGameRoomGroup = null;
            return false;
        }
    }

    Group joinGroupSync(String groupId) throws ExecutionException, InterruptedException {
        client.joinGroup(session, groupId).get();
        GroupList groupList = client.listGroups(session, "%").get();
        return groupList.getGroupsList().stream()
                .filter((Group group) -> group.getId().equals(groupId))
                .collect(Collectors.toList()).get(0);
    }

    public boolean joinGameRoomGroupSync(String groupId) {
        try {
            currentGameRoomGroup = joinGroupSync(groupId);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGameRoomGroup = null;
            return false;
        }
    }

    void leaveGroupSync(String groupId) {
        try {
            client.leaveGroup(session, groupId).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }

    public void leaveGameRoomGroupSync() {
        if (currentGameRoomGroup != null) {
            leaveGroupSync(currentGameRoomGroup.getId());
            currentGameRoomGroup = null;
        }
    }
    //

    // match
    Match createMatchSync(SocketListener socketListener) throws ExecutionException, InterruptedException {
        socketClient.connect(session, socketListener);
        return socketClient.createMatch().get();
    }

    public boolean createGameRoomMatchSync(SocketListener socketListener) {
        try {
            currentGameRoomMatch = createMatchSync(socketListener);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGameRoomMatch = null;
            return false;
        }
    }


    Match joinMatchSync(SocketListener socketListener, String matchId) throws ExecutionException, InterruptedException {
        socketClient.connect(session, socketListener);
        return socketClient.joinMatch(matchId).get();
    }

    public boolean joinGameRoomMatchSync(SocketListener socketListener, String matchId) {
        try {
            currentGameRoomMatch = joinMatchSync(socketListener, matchId);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGameRoomMatch = null;
            return false;
        }
    }

    void leaveMatchSync(String matchId) {
        try {
            socketClient.leaveMatch(matchId).get();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }

    public void leaveGameRoomMatchSync() {
        leaveMatchSync(currentGameRoomMatch.getMatchId());
        currentGameRoomMatch = null;
    }
    //

    // gameRoom
    boolean createGameRoom(String name, String desc, SocketListener socketListener) {
        boolean result = createGameRoomGroupSync(name, desc) && createGameRoomMatchSync(socketListener);
        if (!result) {
            if (currentGameRoomMatch != null) {
                leaveGameRoomGroupSync();
            }
            if (currentGameRoomGroup != null) {
                leaveGameRoomMatchSync();
            }
            return false;
        }
        return true;
    }

    // join
    void joinGameRoom(String groupId, SocketListener socketListener) {

    }

    void leaveGameRoom(){
        leaveGameRoomMatchSync();
        leaveGameRoomGroupSync();
    }

    private List<GameFlag> requestFlagMap(MapRange mapRange) {
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
        try {
            Response<List<AddressDto>> response = requester.execute();
            if (!response.isSuccessful()) {
                return null;
            }
            List<AddressDto> addressDtoList = response.body();
            assert addressDtoList != null;
            return addressDtoList.stream().map(addressDto -> {
                Location location = new Location("");
                location.setLatitude(addressDto.getLatitude());
                location.setLongitude(addressDto.getLongitude());
                return new GameFlag(location);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }


    public boolean createFlagGameRoom(String name, String desc, MapRange mapRange, Duration timeLimit) {
        // 맵 받아오기
        List<GameFlag> gameFlagList = requestFlagMap(mapRange);
        if (gameFlagList == null) {
            return false;
        }
        // 방 만들기
        if (!createGameRoom(name, desc, currentGameRoomOperator)) {
            return false;
        }
        // 진행자의 설정
        currentGameRoomOperator = new FlagGameRoomPlayOperator(
                this,
                timeLimit,
                gameFlagList
        );
        // 방 데이터 준비
        Map<String, Object> payload = new HashMap<>();
        RoomInfo roomInfo = new RoomInfo(
                timeLimit.getSeconds(),
                GameType.GAME_TYPE_FLAG,
                mapRange,
                currentGameRoomMatch.getMatchId(),
                currentGameRoomGroup.getId()
        );
        PrepareDataFlagGameRoom prepareDataFlagGameRoom = new PrepareDataFlagGameRoom(gameFlagList);
        // 방 데이터 입력
        payload.put("info", roomInfo);
        payload.put("prepare", prepareDataFlagGameRoom);
        try {
            Rpc rpcResult = client.rpc(session, "UpdateGroupMetadata", new Gson().toJson(payload, payload.getClass())).get();
        } catch (ExecutionException | InterruptedException ex) {
            leaveGameRoom();
            currentGameRoomOperator = null;
            return false;
        }
        return true;
    }

    public boolean joinFlagGameRoom(String name, String desc, MapRange mapRange, Duration timeLimit) {
        // 맵 받아오기
        List<GameFlag> gameFlagList = requestFlagMap(mapRange);
        if (gameFlagList == null) {
            return false;
        }
        // 방 만들기
        if (!createGameRoom(name, desc, currentGameRoomOperator)) {
            return false;
        }
        // 진행자의 설정
        currentGameRoomOperator = new FlagGameRoomPlayOperator(
                this,
                timeLimit,
                gameFlagList
        );
        // 방 데이터 준비
        Map<String, Object> payload = new HashMap<>();
        RoomInfo roomInfo = new RoomInfo(
                timeLimit.getSeconds(),
                GameType.GAME_TYPE_FLAG,
                mapRange,
                currentGameRoomMatch.getMatchId(),
                currentGameRoomGroup.getId()
        );
        PrepareDataFlagGameRoom prepareDataFlagGameRoom = new PrepareDataFlagGameRoom(gameFlagList);
        // 방 데이터 입력
        payload.put("info", roomInfo);
        payload.put("prepare", prepareDataFlagGameRoom);
        try {
            Rpc rpcResult = client.rpc(session, "UpdateGroupMetadata", new Gson().toJson(payload, payload.getClass())).get();
        } catch (ExecutionException | InterruptedException ex) {
            leaveGameRoom();
            currentGameRoomOperator = null;
            return false;
        }
        return true;
    }

    public String getGameRoomMatchId() {
        return currentGameRoomMatch.getMatchId();
    }

    public String getGameRoomGroupId() {
        return currentGameRoomGroup.getId();
    }

    public String getCurrentUserId() {
        return session.getUserId();
    }

    public GroupUserList getGameRoomGroupUserList() {
        try {
            return client.listGroupUsers(session, currentGameRoomGroup.getId()).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public GroupList getGameRoomGroupList() {
        try {
            return client.listGroups(session, "%").get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }
}
