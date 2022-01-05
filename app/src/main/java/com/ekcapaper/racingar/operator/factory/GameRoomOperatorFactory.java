package com.ekcapaper.racingar.operator.factory;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameRoomOperatorFactory {
    protected static String getMatchCollectionName(String matchId){
        return "match-"+matchId;
    }

    protected static String getGameFlagListKeyName(){
        return "GameFlagList";
    }

    public abstract GameRoomOperator createRoom();
    public abstract GameRoomOperator joinRoom(String matchId);
}
