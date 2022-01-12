package com.ekcapaper.racingar.modelgame.play;

import android.location.Location;

import java.util.Objects;
import java.util.Optional;

import lombok.Getter;

public class Player {
    @Getter
    String userId;
    @Getter
    Optional<Location> location;

    public Player(String userId) {
        this.userId = userId;
    }

    public void updateLocation(Location location){
        this.location = Optional.ofNullable(location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(userId, player.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
