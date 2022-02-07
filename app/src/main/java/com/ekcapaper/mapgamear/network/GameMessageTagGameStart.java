package com.ekcapaper.mapgamear.network;

import lombok.Getter;

public class GameMessageTagGameStart extends GameMessageStart {
    @Getter
    private final String taggerUserId;

    public GameMessageTagGameStart(String taggerUserId) {
        this.taggerUserId = taggerUserId;
    }
}
