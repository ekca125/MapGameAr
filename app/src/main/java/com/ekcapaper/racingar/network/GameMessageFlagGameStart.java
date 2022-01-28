package com.ekcapaper.racingar.network;

import static com.ekcapaper.racingar.network.GameMessageOpCode.FLAG_GAME_START;

import com.ekcapaper.racingar.modelgame.play.GameFlag;

import java.util.List;

import lombok.Getter;

public class GameMessageFlagGameStart extends GameMessageStart{
    @Getter
    private final List<GameFlag> gameFlagList;

    public GameMessageFlagGameStart(List<GameFlag> gameFlagList) {
        super(FLAG_GAME_START);
        this.gameFlagList = gameFlagList;
    }
}
