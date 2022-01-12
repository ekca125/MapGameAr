package com.ekcapaper.racingar.modelgame.gameroom;

import com.ekcapaper.racingar.modelgame.play.GameType;

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
