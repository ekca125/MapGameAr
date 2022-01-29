package com.ekcapaper.mapgamear.network;

public class GameMessageEnd extends GameMessage {
    public GameMessageEnd() {
        super(GameMessageOpCode.GAME_END);
    }
}
