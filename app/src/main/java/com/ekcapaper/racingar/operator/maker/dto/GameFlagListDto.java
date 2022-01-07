package com.ekcapaper.racingar.operator.maker.dto;

import com.ekcapaper.racingar.game.GameFlag;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameFlagListDto {
    List<GameFlag> gameFlagList;

    public GameFlagListDto(List<GameFlag> gameFlagList) {
        this.gameFlagList = gameFlagList;
    }
}
