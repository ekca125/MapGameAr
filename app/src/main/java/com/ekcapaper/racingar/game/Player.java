package com.ekcapaper.racingar.game;

import android.location.Location;

import java.util.Optional;

import lombok.Getter;

public class Player {
    String userId;
    Optional<Location> location;

    public Player(String userId) {
        this.userId = userId;
    }

    public void updateLocation(Location location){
        this.location = Optional.ofNullable(location);
    }

    public Optional<Location> getLocation() {
        return location;
    }
}
