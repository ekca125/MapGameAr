package com.ekcapaper.mapgamear.modelgame.play;

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
        this.location = Optional.empty();
    }

    public void updateLocation(Location location) {
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
