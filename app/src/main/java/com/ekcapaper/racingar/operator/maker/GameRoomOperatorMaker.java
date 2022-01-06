package com.ekcapaper.racingar.operator.maker;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameRoomOperatorMaker {
    protected static String getMatchCollectionName(String matchId){
        return "match-"+matchId;
    }
    protected static String getGameFlagListKeyName(){
        return "GameFlagList";
    }

    public abstract void prepare();
    public abstract GameRoomOperator make();
}
