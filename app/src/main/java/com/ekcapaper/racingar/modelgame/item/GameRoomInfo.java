package com.ekcapaper.racingar.modelgame.item;

import android.graphics.drawable.Drawable;

import lombok.Builder;

public class GameRoomInfo {
    public String name;
    @Builder
    public GameRoomInfo(String name) {
        this.name = name;
    }
}
