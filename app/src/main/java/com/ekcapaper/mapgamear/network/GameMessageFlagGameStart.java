package com.ekcapaper.mapgamear.network;

import static com.ekcapaper.mapgamear.network.GameMessageOpCode.FLAG_GAME_START;

import com.ekcapaper.mapgamear.modelgame.play.GameFlag;

import java.util.List;

import lombok.Getter;

public class GameMessageFlagGameStart extends GameMessageStart{
    @Getter
    private final List<GameFlag> gameFlagList;

    public GameMessageFlagGameStart(List<GameFlag> gameFlagList) {
        super();
        this.gameFlagList = gameFlagList;
    }
}
