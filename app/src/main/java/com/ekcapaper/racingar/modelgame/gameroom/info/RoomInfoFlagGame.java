package com.ekcapaper.racingar.modelgame.gameroom.info;

import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.retrofit.dto.MapRange;

import lombok.Getter;

public class RoomInfoFlagGame extends RoomInfoTimeLimit {
    @Getter
    MapRange mapRange;

    public RoomInfoFlagGame(long timeLimitSeconds, GameType gameType, MapRange mapRange) {
        super(timeLimitSeconds, gameType);
        this.mapRange = mapRange;
    }
}
