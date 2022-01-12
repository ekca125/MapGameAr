package com.ekcapaper.racingar.model;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.modelgame.play.GameType;

public class GameLobbyRoomInfo {
    // info
    public String name;
    // map info
    public String distanceCenter;
    public GameType gameType;

    public int getImage(){
        switch (gameType){
            case GAME_TYPE_FLAG:
                return R.drawable.image_2;
        }
        return R.drawable.image_2;
    }

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
