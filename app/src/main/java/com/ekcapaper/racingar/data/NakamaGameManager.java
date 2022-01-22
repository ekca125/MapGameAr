package com.ekcapaper.racingar.data;

import com.ekcapaper.racingar.network.GameMessage;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.Rpc;
import com.heroiclabs.nakama.api.StorageObjectAcks;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.NonNull;

public class NakamaGameManager{
    private final NakamaNetworkManager nakamaNetworkManager;
    private final NakamaRoomMetaDataManager nakamaRoomMetaDataManager;
    private Group roomGroup;
    private Match roomMatch;
    private SocketListener roomOperator;

    public NakamaGameManager(NakamaNetworkManager nakamaNetworkManager) {
        this.nakamaNetworkManager = nakamaNetworkManager;
        this.nakamaRoomMetaDataManager = new NakamaRoomMetaDataManager(nakamaNetworkManager);
        this.roomGroup = null;
        this.roomMatch = null;
        this.roomOperator = null;
    }

    public boolean isActive(){
        return roomOperator != null || roomGroup != null || roomMatch != null;
    }

    public boolean createGameRoom(@NonNull String roomName, @NonNull String roomDesc, @NonNull SocketListener socketListener){
        if(isActive()){
            // 이미 활성화 된 상태라면 오류 발생
            throw new IllegalStateException();
        }
        // 그룹과 매치를 받아오기
        Group group = nakamaNetworkManager.createGroupSync(roomName,roomDesc);
        Match match = nakamaNetworkManager.createMatchSync(socketListener);
        // 정상 여부의 확인
        if(group == null || match == null){
            if(group != null){
                nakamaNetworkManager.leaveGroupSync(group.getName());
            }
            if(match != null){
                nakamaNetworkManager.leaveMatchSync(match.getMatchId());
            }
            return false;
        }
        // 그룹 메타 데이터 쓰기
        Map<String,Object> metadata = new HashMap<>();
        metadata.put("groupId", group.getId());
        metadata.put("matchId", match.getMatchId());
        nakamaRoomMetaDataManager.writeRoomMetaDataSync(group,metadata);

        // 객체에 반영
        this.roomOperator = socketListener;
        this.roomGroup = group;
        this.roomMatch = match;
        return true;
    }

    public void leaveGameRoom(){
        if(!isActive()){
            return;
        }
        // 떠나기
        this.nakamaNetworkManager.leaveMatchSync(roomMatch.getMatchId());
        this.nakamaNetworkManager.leaveGroupSync(roomGroup.getName());
        // 초기화
        this.roomGroup = null;
        this.roomMatch = null;
        this.roomOperator = null;
    }

    public boolean joinGameRoom(@NonNull String roomName, @NonNull SocketListener socketListener){
        if(isActive()){
            // 이미 활성화 된 상태라면 오류 발생
            throw new IllegalStateException();
        }
        // 그룹 가입
        Group group = nakamaNetworkManager.joinGroupSync(roomName);
        if(group == null){
            return false;
        }
        // 메타 데이터 받아오기
        Map<String,Object> metadata = nakamaRoomMetaDataManager.readRoomMetaDataSync(group);
        String matchId = (String) metadata.get("matchId");

        Match match = nakamaNetworkManager.joinMatchSync(socketListener,matchId);
        if(match == null){
            nakamaNetworkManager.leaveGroupSync(group.getName());
            return false;
        }
        //
        this.roomOperator = socketListener;
        this.roomGroup = group;
        this.roomMatch = match;
        return true;
    }

    public void sendGameRoomGameMessage(GameMessage gameMessage){
        nakamaNetworkManager.socketClient.sendMatchData(
                nakamaNetworkManager.getCurrentSessionUserId(),
                gameMessage.getOpCode().ordinal(),
                gameMessage.getPayload().getBytes(StandardCharsets.UTF_8)
        );
    }

    public GroupUserList getGameRoomGroupUserList(){
        return nakamaNetworkManager.getGroupUserList(roomGroup.getName());
    }
}
