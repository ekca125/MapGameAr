package com.ekcapaper.racingar.modelgame.gameroom.prepare;

import com.ekcapaper.racingar.modelgame.play.GameFlag;

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
