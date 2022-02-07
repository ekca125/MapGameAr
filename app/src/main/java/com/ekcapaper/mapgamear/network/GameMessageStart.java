package com.ekcapaper.mapgamear.network;

public class GameMessageStart extends GameMessage {
    public GameMessageStart() {
        super(GameMessageOpCode.GAME_START);
    }
}
