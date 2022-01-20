package com.ekcapaper.racingar.data;

import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.api.Group;

import lombok.Getter;
import lombok.NonNull;

public class NakamaGameManager{
    private NakamaNetworkManager nakamaNetworkManager;
    private Group roomGroup;
    private Match roomMatch;
    private GameRoomPlayOperator roomOperator;

    public NakamaGameManager(NakamaNetworkManager nakamaNetworkManager) {
        this.nakamaNetworkManager = nakamaNetworkManager;
        this.roomGroup = null;
        this.roomMatch = null;
        this.roomOperator = null;
    }

    public boolean isActive(){
        return roomOperator != null || roomGroup != null || roomMatch != null;
    }

    public boolean createGameRoom(@NonNull String roomName, @NonNull String roomDesc, @NonNull GameRoomPlayOperator gameRoomPlayOperator){
        if(isActive()){
            // 이미 활성화 된 상태라면 오류 발생
            throw new IllegalStateException();
        }
        // 그룹과 매치를 받아오기
        Group group = nakamaNetworkManager.createGroupSync(roomName,roomDesc);
        Match match = nakamaNetworkManager.createMatchSync(gameRoomPlayOperator);
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
        // 객체에 반영
        this.roomOperator = gameRoomPlayOperator;
        this.roomGroup = group;
        this.roomMatch = match;
        return true;
    }

}
