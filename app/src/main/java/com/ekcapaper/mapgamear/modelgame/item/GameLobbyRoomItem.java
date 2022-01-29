package com.ekcapaper.mapgamear.modelgame.item;

import lombok.Builder;

@Builder
public class GameLobbyRoomItem {
    public String roomName;
    public String roomDesc;
    public String distanceCenter;
    public String matchId;
    public String gameTypeDesc;

    @Builder
    public GameLobbyRoomItem(String roomName, String roomDesc, String distanceCenter, String matchId, String gameTypeDesc) {
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.distanceCenter = distanceCenter;
        this.matchId = matchId;
        this.gameTypeDesc = gameTypeDesc;
    }
}
