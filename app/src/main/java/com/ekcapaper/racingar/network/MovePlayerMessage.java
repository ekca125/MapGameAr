package com.ekcapaper.racingar.network;

import com.google.gson.Gson;

import lombok.Builder;

public class MovePlayerMessage extends Message {
    private final String userId;
    private final double latitude;
    private final double longitude;

    @Builder
    public MovePlayerMessage(String userId, double latitude, double longitude) {
        super(OpCode.MOVE_PLAYER);
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getPayload() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
