package com.ekcapaper.racingar.modelgame.gameroom;

public class RoomDataSpace {
    public static String normalizeMatchId (String matchId) {
        return matchId.replace(".", "");
    }

    public static String getCollectionName(String matchId) {
        return "Match-" + normalizeMatchId(matchId);
    }

    public static String getDataRoomInfoKey() {
        return "Room-Info";
    }

    public static String getDataRoomPrepareKey() {
        return "Room-Prepare";
    }

}
