package com.ekcapaper.mapgamear.network;

import com.google.gson.Gson;

public abstract class GameMessage {
    private final GameMessageOpCode gameMessageOpCode;

    public GameMessage(GameMessageOpCode gameMessageOpCode) {
        this.gameMessageOpCode = gameMessageOpCode;
    }

    public GameMessageOpCode getOpCode() {
        return gameMessageOpCode;
    }

    public String getPayload() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
