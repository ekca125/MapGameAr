package com.ekcapaper.racingar.game;

import com.ekcapaper.racingar.game.GameType;

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
