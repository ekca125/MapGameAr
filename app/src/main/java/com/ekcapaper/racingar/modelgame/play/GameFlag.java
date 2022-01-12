package com.ekcapaper.racingar.modelgame.play;

import android.location.Location;

public class GameFlag {
    // 소유권
    boolean owned;
    String userId;
    // 위치
    Location location;
    // 소유권을 주장하는 플레이어 위치와의 거리 (50m)
    private final double ownMeter = 50;
    
    public GameFlag(Location location) {
        this.location = location;
        this.owned = false;
        this.userId = "";
    }

    public boolean isOwned() {
        return owned;
    }

    public String getUserId() {
        return userId;
    }

    public void reflectPlayerLocation(Location playerLocation, String userId){
        if(!owned){
            double distance = playerLocation.distanceTo(location);
            if(distance <= ownMeter){
                this.owned = true;
                this.userId = userId;
            }
        }
    }

    public Location getLocation() {
        return location;
    }
}
