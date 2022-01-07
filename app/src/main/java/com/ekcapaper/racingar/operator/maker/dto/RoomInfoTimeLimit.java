package com.ekcapaper.racingar.operator.maker.dto;

import lombok.Getter;

public class RoomInfoTimeLimit {
    @Getter
    long timeLimitSeconds;

    public RoomInfoTimeLimit(long timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
