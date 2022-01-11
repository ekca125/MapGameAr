package com.ekcapaper.racingar.model;

import android.graphics.drawable.Drawable;

import com.ekcapaper.racingar.game.GameType;

public class GameLobbyRoomInfo {
    // info
    public String name;
    public int image;
    public Drawable imageDrw;
    // map info
    public String distanceCenter;
    public GameType gameType;
    public String getGameTypeString(){
        switch (gameType){
            case GAME_TYPE_FLAG:
                return "Flag Game";
        }
        return "";
    }

    public GameLobbyRoomInfo() {
    }
}
