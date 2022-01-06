package com.ekcapaper.racingar.operator.maker;

import java.time.Duration;

import lombok.Getter;

public abstract class TimeLimitGameRoomOperatorMaker extends GameRoomOperatorMaker {
    @Getter
    private final Duration timeLimit;

    protected TimeLimitGameRoomOperatorMaker(Duration timeLimit) {
        this.timeLimit = timeLimit;
    }
}
