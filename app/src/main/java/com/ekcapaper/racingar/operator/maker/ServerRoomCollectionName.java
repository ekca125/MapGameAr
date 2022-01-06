package com.ekcapaper.racingar.operator.maker;

public class ServerRoomCollectionName {
    public static String getCollectionName(String matchId){
        return "Match-"+matchId;
    }
}
