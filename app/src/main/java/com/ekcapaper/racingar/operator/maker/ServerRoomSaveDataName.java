package com.ekcapaper.racingar.operator.maker;

public class ServerRoomSaveDataName {
    public static String getCollectionName(String matchId){
        return "Match-"+matchId;
    }
    public static String getGameFlagList(){ return "GameFlagList";}
}
