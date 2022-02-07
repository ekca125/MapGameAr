package com.ekcapaper.mapgamear.modelgame;

import com.ekcapaper.mapgamear.modelgame.address.MapRange;
import com.ekcapaper.mapgamear.modelgame.play.GameType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    @Getter
    final int timeLimitSecond;
    @Getter
    boolean opened;

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude,
            String masterUserId,
            GameType gameType,
            boolean opened,
            int timeLimitSecond) {
        super(startLatitude, startLongitude, endLatitude, endLongitude);
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.masterUserId = masterUserId;
        this.gameType = gameType;
        this.opened = opened;
        this.timeLimitSecond = timeLimitSecond;
    }

    public GameRoomLabel(
            String roomName,
            String roomDesc,
            MapRange mapRange,
            String masterUserId,
            GameType gameType,
            boolean opened,
            int timeLimitSecond
    ) {
        this(
                roomName,
                roomDesc,
                mapRange.getStartLatitude(),
                mapRange.getStartLongitude(),
                mapRange.getEndLatitude(),
                mapRange.getEndLongitude(),
                masterUserId,
                gameType,
                opened,
                timeLimitSecond
        );
    }

    public String getTimeLimit_ISO_LOCAL_TIME() {
        LocalTime timeOfDay = LocalTime.ofSecondOfDay(this.getTimeLimitSecond());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        return timeOfDay.format(dateTimeFormatter);
    }

}
