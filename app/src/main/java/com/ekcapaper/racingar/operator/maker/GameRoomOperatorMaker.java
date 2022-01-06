package com.ekcapaper.racingar.operator.maker;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameRoomOperatorMaker {
    protected static String getMatchCollectionName(String matchId){
        return "match-"+matchId;
    }

    protected static String getGameFlagListKeyName(){
        return "GameFlagList";
    }

    public abstract GameRoomOperator createRoom();
    public abstract GameRoomOperator joinRoom(String matchId);

    protected abstract void prepare();
    protected abstract void writeServerStorage();
    public abstract GameRoomOperator make();
}
