package com.ekcapaper.racingar.operator.maker;

public class ServerRoomSaveDataNameSpace {
    public static String getCollectionName(String matchId){
        return "Match-"+matchId;
    }
    public static String getGameFlagListName(){ return "GameFlagList";}
}
