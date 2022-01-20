package com.ekcapaper.racingar.data;

import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.Rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.NonNull;

public class NakamaGameManager{
    private NakamaNetworkManager nakamaNetworkManager;
    private Group roomGroup;
    private Match roomMatch;
    private SocketListener roomOperator;

    public NakamaGameManager(NakamaNetworkManager nakamaNetworkManager) {
        this.nakamaNetworkManager = nakamaNetworkManager;
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
        // 메타 데이터 받아오기

        return false;
    }
}
