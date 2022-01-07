package com.ekcapaper.racingar.operator.maker;

public class SaveDataNameDefine {
    public static String getCollectionName(String matchId) {
        return "Match-" + matchId.replace(".", "");
    }

    public static String getDataRoomInfoKey() {
        return "Room-Info";
    }

    public static String getDataRoomPrepareKey() {
        return "Room-Prepare";
    }

}
