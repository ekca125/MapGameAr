package com.ekcapaper.racingar.network;

public class GameEndMessage extends Message{
    public GameEndMessage() {
        super(OpCode.GAME_END);
    }
}
