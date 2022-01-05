package com.ekcapaper.racingar.operator.factory;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameRoomOperatorFactory {
    

    public abstract GameRoomOperator createRoom();
    public abstract GameRoomOperator joinRoom();
}
