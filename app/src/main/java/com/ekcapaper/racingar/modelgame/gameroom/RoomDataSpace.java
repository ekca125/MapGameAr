package com.ekcapaper.racingar.modelgame.gameroom;

public class RoomDataSpace {
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
