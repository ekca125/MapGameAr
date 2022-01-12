package com.ekcapaper.racingar.network;

public class GameMessageEnd extends GameMessage {
    public GameMessageEnd() {
        super(GameMessageOpCode.GAME_END);
    }
}
