package com.ekcapaper.racingar.operator.maker.data;

import com.ekcapaper.racingar.game.GameFlag;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepareDataFlagGameRoom {
    List<GameFlag> gameFlagList;

    public PrepareDataFlagGameRoom(List<GameFlag> gameFlagList) {
        this.gameFlagList = gameFlagList;
    }
}
