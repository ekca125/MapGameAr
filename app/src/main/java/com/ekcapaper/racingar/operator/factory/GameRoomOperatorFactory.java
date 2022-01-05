package com.ekcapaper.racingar.operator.factory;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameRoomOperatorFactory {
    public static String getMatchCollectionName(String matchId){
        
    }


    private String getMatchCollectionName(String matchId){
        return "match-" + matchId;
    }

    private String getGameFlagListName(){
        return "GameFlagList";
    }

    public abstract GameRoomOperator createRoom();
    public abstract GameRoomOperator joinRoom();
}
