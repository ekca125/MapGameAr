package com.ekcapaper.racingar.operator;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;

public class FlagGameRoomOperator extends GameRoomOperator{
    private final Duration timeLimit;

    public FlagGameRoomOperator(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
    }

    @Override
    boolean isEnd() {
        return false;
    }
}
