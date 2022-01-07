package com.ekcapaper.racingar.operator.maker.dto;

import lombok.Getter;
import lombok.Setter;

public class PrepareDataTimeLimit {
    @Getter
    long timeLimitSeconds;

    public PrepareDataTimeLimit(long timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
