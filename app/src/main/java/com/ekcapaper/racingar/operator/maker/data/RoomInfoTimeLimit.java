package com.ekcapaper.racingar.operator.maker.data;

import lombok.Getter;

public class RoomInfoTimeLimit {
    @Getter
    long timeLimitSeconds;

    public RoomInfoTimeLimit(long timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
