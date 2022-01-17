package com.ekcapaper.racingar.modelgame.gameroom;

public class RoomDataSpace {
    public static String normalizeMatchId (String matchId) {
        return matchId.replaceAll("[^A-z0-9-]","");
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
