package com.ekcapaper.racingar.modelgame;

import com.ekcapaper.racingar.modelgame.address.MapRange;

public class GameRoomLabel extends MapRange {
    String roomName;
    String roomDesc;

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude) {
        super(startLatitude, startLongitude, endLatitude, endLongitude);
        this.roomName = roomName;
        this.roomDesc = roomDesc;
    }

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            MapRange mapRange
    ) {
        this(
                roomName,
                roomDesc,
                mapRange.getStartLatitude(),
                mapRange.getStartLongitude(),
                mapRange.getEndLatitude(),
                mapRange.getEndLongitude()
        );
    }
}
