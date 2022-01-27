package com.ekcapaper.racingar.network;

import com.ekcapaper.racingar.modelgame.play.GameFlag;

import java.util.List;

import lombok.Getter;

public class GameMessageFlagGameStart extends GameMessageStart{
    @Getter
    private List<GameFlag> gameFlagList;

    public GameMessageFlagGameStart(List<GameFlag> gameFlagList) {
        this.gameFlagList = gameFlagList;
    }
}
