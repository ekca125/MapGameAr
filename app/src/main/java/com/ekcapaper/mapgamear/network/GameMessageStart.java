package com.ekcapaper.mapgamear.network;

import com.google.gson.Gson;

public class GameMessageStart extends GameMessage {
    public GameMessageStart() {
        super(GameMessageOpCode.GAME_START);
    }
}
