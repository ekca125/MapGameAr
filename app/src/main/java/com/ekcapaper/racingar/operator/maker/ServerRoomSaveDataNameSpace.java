package com.ekcapaper.racingar.operator.maker;

public class ServerRoomSaveDataNameSpace {
    public static String getCollectionName(String matchId) {
        return "Match-" + matchId;
    }

    public static String getRoomPrepareDataName() {
        return "Prepare";
    }

    public static String getGameFlagListJsonKey() { return "flags";}

    public static String getMapRangeKey() { return "mapRange";}
}
