package com.ekcapaper.racingar.network;

import lombok.Builder;

public class MovePlayerMessage extends Message {
    String userId;
    String latitude;
    String longitude;

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
