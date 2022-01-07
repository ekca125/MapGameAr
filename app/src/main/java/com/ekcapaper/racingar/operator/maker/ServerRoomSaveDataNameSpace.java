package com.ekcapaper.racingar.operator.maker;

public class ServerRoomSaveDataNameSpace {
    public static String getCollectionName(String matchId) {
        return "Match-" + matchId.replace(".", "");
    }

    public static String getRoomPrepareKeyTimeLimit() {return "Prepare-TimeLimit";}

    public static String getRoomPrepareKeyGameFlagListName() {
        return "Prepare-GameFlag";
    }

    public static String getRoomPrepareKeyMapRangeName() {
        return "Prepare-MapRange";
    }
}
