package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;
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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import retrofit2.Call;
import retrofit2.Response;

public class ThisApplication extends Application {
    @Getter
    private Client client;
    @Getter
    private Session session;
    @Getter
    private SocketClient socketClient;
    // group, match
    @Getter
    private Group currentGroup;
    @Getter
    private Match currentMatch;
    @Getter
    private GameRoomPlayOperator currentGameRoomOperator;
    @Getter
    private ExecutorService executorService;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = null;
        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        currentGroup = null;
        currentMatch = null;
        currentGameRoomOperator = null;
        executorService = Executors.newFixedThreadPool(4);
    }

    public String getCurrentMatchId(){
        return currentMatch.getMatchId();
    }

    public String getCurrentGroupId(){
        return currentGroup.getId();
    }

    public String getCurrentUserId(){
        return session.getUserId();
    }

    public GroupUserList getCurrentGroupUserList(){
        try {
            return client.listGroupUsers(session,currentGroup.getId()).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
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

    public void loginEmail(String email, String password, FutureCallback<Session> futureCallback) {
        val future = client.authenticateEmail(email, password);
        Futures.addCallback(future, new FutureCallback<Session>() {
            @Override
            public void onSuccess(@Nullable Session result) {
                ThisApplication.this.session = result;
            }

            @Override
            public void onFailure(Throwable t) {
                session = null;
            }
        }, executorService);
        Futures.addCallback(future, futureCallback, executorService);
    }

    public boolean createGroupSync(String name, String desc){
        try {
            currentGroup = client.createGroup(session,name,desc,null,null,true).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGroup = null;
            return false;
        }
    }

    public boolean joinGroupSync(String groupId){
        try {
            client.joinGroup(session,groupId).get();
            GroupList groupList = client.listGroups(session, "%").get();
            currentGroup = groupList.getGroupsList().stream()
                    .filter((Group group) -> group.getId().equals(groupId))
                    .collect(Collectors.toList()).get(0);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            currentGroup = null;
            return false;
        } catch(IndexOutOfBoundsException e){
            client.leaveGroup(session,groupId);
            currentGroup = null;
            return false;
        }
    }

    public boolean leaveGroupSync(String groupId){
        try {
            client.leaveGroup(session,groupId).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public boolean leaveCurrentGroupSync(){
        if(currentGroup != null){
            return leaveGroupSync(currentGroup.getId());
        }
        else{
            return false;
        }
    }

    public boolean createMatchSync(SocketListener socketListener){
        try {
            socketClient.connect(session,socketListener);
            currentMatch = socketClient.createMatch().get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public boolean joinMatchSync(SocketListener socketListener, String matchId){
        try {
            socketClient.connect(session,socketListener);
            currentMatch = socketClient.joinMatch(matchId).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public boolean leaveMatchSync(String matchId){
        try {
            socketClient.leaveMatch(matchId).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public boolean leaveCurrentMatchSync(){
        if(currentMatch != null){
            return leaveMatchSync(currentMatch.getMatchId());
        }
        else{
            return false;
        }
    }

    private boolean createGameRoom(String name, String desc, SocketListener socketListener){
        boolean result = createGroupSync(name, desc) && createMatchSync(socketListener);
        if(!result){
            if(currentMatch != null){
                leaveCurrentMatchSync();
            }
            if(currentGroup != null){
                leaveCurrentGroupSync();
            }
        }
        return false;
    }

    private List<GameFlag> requestFlagMap(MapRange mapRange){
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


    public boolean createFlagGameRoom(String name, String desc, MapRange mapRange, Duration timeLimit){
        // 맵 받아오기
        List<GameFlag> gameFlagList = requestFlagMap(mapRange);
        if(gameFlagList == null){
            return false;
        }
        // 방 만들기
        if(!createGameRoom(name,desc,currentGameRoomOperator)){
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
                currentMatch.getMatchId(),
                currentGroup.getId()
        );
        PrepareDataFlagGameRoom prepareDataFlagGameRoom = new PrepareDataFlagGameRoom(gameFlagList);
        // 방 데이터 입력
        payload.put("info", roomInfo);
        payload.put("prepare",prepareDataFlagGameRoom);
        try {
            Rpc rpcResult = client.rpc(session, "UpdateGroupMetadata", new Gson().toJson(payload, payload.getClass())).get();
        }
        catch (ExecutionException | InterruptedException ex) {
            leaveCurrentGroupSync();
            leaveCurrentMatchSync();
            currentGameRoomOperator = null;
            return false;
        }
        return true;
    }
}
