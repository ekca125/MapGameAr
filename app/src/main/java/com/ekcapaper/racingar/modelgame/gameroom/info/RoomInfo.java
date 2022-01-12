package com.ekcapaper.racingar.modelgame.gameroom.info;

import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.modelgame.address.MapRange;

import lombok.Getter;

@Getter
public final class RoomInfo {
    long timeLimitSeconds;
    GameType gameType;
    MapRange mapRange;
    String matchId;

    public RoomInfo(long timeLimitSeconds, GameType gameType, MapRange mapRange, String matchId) {
        this.timeLimitSeconds = timeLimitSeconds;
        this.gameType = gameType;
        this.mapRange = mapRange;
        this.matchId = matchId;
    }
}
