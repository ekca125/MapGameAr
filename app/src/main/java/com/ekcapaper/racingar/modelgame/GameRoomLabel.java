package com.ekcapaper.racingar.modelgame;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameType;

import lombok.Getter;

public class GameRoomLabel extends MapRange {
    @Getter
    final String roomName;
    @Getter
    final String roomDesc;
    @Getter
    final String masterUserId;
    @Getter
    final GameType gameType;

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude,
            String masterUserId,
            GameType gameType) {
        super(startLatitude, startLongitude, endLatitude, endLongitude);
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.masterUserId = masterUserId;
        this.gameType = gameType;
    }

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            MapRange mapRange,
            String masterUserId,
            GameType gameType
    ) {
        this(
                roomName,
                roomDesc,
                mapRange.getStartLatitude(),
                mapRange.getStartLongitude(),
                mapRange.getEndLatitude(),
                mapRange.getEndLongitude(),
                masterUserId,
                gameType
        );
    }
}
