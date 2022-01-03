package com.ekcapaper.racingar.network;

import android.location.Location;

import com.google.gson.Gson;

import lombok.Builder;
import lombok.Getter;

@Getter
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

    public Location getLocation(){
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
