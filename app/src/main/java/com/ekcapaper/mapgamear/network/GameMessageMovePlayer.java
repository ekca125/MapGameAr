package com.ekcapaper.mapgamear.network;

import android.location.Location;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GameMessageMovePlayer extends GameMessage {
    private final String userId;
    private final double latitude;
    private final double longitude;

    @Builder
    public GameMessageMovePlayer(String userId, double latitude, double longitude) {
        super(GameMessageOpCode.MOVE_PLAYER);
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
