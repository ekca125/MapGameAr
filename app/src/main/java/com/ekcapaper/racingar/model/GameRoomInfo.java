package com.ekcapaper.racingar.model;

import android.graphics.drawable.Drawable;

public class GameRoomInfo {

    public int image;
    public Drawable imageDrw;
    public String name;
    public boolean expanded = false;
    public boolean parent = false;

    // flag when item swiped
    public boolean swiped = false;

    public GameRoomInfo() {
    }

    public GameRoomInfo(int image, String name) {
        this.image = image;
        this.name = name;
    }
}
