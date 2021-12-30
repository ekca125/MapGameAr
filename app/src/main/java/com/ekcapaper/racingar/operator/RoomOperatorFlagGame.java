package com.ekcapaper.racingar.operator;

import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

public class RoomOperatorFlagGame extends RoomOperator{
    public RoomOperatorFlagGame(Session session, SocketClient socketClient, Match match) {
        super(session, socketClient, match);
    }

    @Override
    protected boolean isEnd() {
        return false;
    }

    @Override
    protected boolean isVictory() {
        return false;
    }

    @Override
    protected boolean isDefeat() {
        return false;
    }
}
