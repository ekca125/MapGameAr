package com.ekcapaper.racingar.network;

import com.google.gson.Gson;

public class GameStartMessage extends Message{
    int limitTimeSecond;

    public GameStartMessage() {
        super(OpCode.GAME_START);
        limitTimeSecond = 0;
    }

    @Override
    public String getPayload() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getLimitTimeSecond() {
        return limitTimeSecond;
    }
}
