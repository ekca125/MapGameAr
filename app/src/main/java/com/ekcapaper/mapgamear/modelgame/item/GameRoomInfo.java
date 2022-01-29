package com.ekcapaper.mapgamear.modelgame.item;

import lombok.Builder;

public class GameRoomInfo {
    public String userName;
    public String userId;

    @Builder
    public GameRoomInfo(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }
}
