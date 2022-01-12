package com.ekcapaper.racingar.modelgame;

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
