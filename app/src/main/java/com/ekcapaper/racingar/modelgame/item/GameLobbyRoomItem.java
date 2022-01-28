package com.ekcapaper.racingar.modelgame.item;

import lombok.Builder;

@Builder
public class GameLobbyRoomItem {
    public String roomName;
    public String roomDesc;
    public String distanceCenter;

    @Builder
    public GameLobbyRoomItem(String roomName, String roomDesc, String distanceCenter) {
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.distanceCenter = distanceCenter;
    }
}
