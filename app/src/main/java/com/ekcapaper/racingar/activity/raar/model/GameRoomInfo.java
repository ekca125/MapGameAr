package com.ekcapaper.racingar.activity.raar.model;

import android.graphics.drawable.Drawable;

public class GameRoomInfo {
    public int image;
    public Drawable imageDrw;
    public String name;

    public GameRoomInfo() {
    }

    public GameRoomInfo(String name) {
        this.name = name;
    }
}
