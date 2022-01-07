package com.ekcapaper.racingar.operator.maker.dto;

import com.ekcapaper.racingar.retrofit.dto.MapRange;

import lombok.Getter;

public class RoomInfoFlagGame extends RoomInfoTimeLimit{
    MapRange mapRange;

    public RoomInfoFlagGame(long timeLimitSeconds, MapRange mapRange) {
        super(timeLimitSeconds);
        this.mapRange = mapRange;
    }
}
