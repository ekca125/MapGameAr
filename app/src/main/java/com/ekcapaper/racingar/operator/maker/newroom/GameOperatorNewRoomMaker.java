package com.ekcapaper.racingar.operator.maker.newroom;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;

public abstract class GameOperatorNewRoomMaker extends Game{






    public abstract GameRoomOperator createRoom();
    public abstract GameRoomOperator joinRoom(String matchId);

    protected abstract void prepare();
    protected abstract void writeServerStorage();
    public abstract GameRoomOperator make();

}
