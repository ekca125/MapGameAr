package com.ekcapaper.racingar.modelgame;

import lombok.Getter;

public class RoomInfoTimeLimit {
    @Getter
    long timeLimitSeconds;
    @Getter
    GameType gameType;

    public RoomInfoTimeLimit(long timeLimitSeconds, GameType gameType) {
        this.timeLimitSeconds = timeLimitSeconds;
        this.gameType = gameType;
    }
}
