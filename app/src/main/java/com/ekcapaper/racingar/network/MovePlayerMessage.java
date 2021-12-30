package com.ekcapaper.racingar.network;

import lombok.Builder;

public class MovePlayerMessage extends Message {
    private final String userId;
    private final String latitude;
    private final String longitude;

    @Builder
    public MovePlayerMessage(String userId, String latitude, String longitude) {
        super(OpCode.MOVE_PLAYER);
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getPayload() {


        return null;
    }
}
