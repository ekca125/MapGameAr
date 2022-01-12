package com.ekcapaper.racingar.modelgame.gameroom.info;

import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.modelgame.address.MapRange;

import lombok.Getter;

@Getter
public final class RoomInfo {
    long timeLimitSeconds;
    GameType gameType;
    MapRange mapRange;

    public RoomInfo(long timeLimitSeconds, GameType gameType, MapRange mapRange) {
        this.timeLimitSeconds = timeLimitSeconds;
        this.gameType = gameType;
        this.mapRange = mapRange;
    }
}
