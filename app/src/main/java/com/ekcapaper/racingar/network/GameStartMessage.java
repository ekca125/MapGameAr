package com.ekcapaper.racingar.network;

import com.google.gson.Gson;

public class GameStartMessage extends Message{
    public GameStartMessage() {
        super(OpCode.GAME_START);
    }

    @Override
    public String getPayload() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
