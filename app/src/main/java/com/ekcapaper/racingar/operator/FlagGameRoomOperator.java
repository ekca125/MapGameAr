package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;

public class FlagGameRoomOperator extends TimeLimitGameRoomOperator {
    public FlagGameRoomOperator(Client client, Session session, Duration timeLimit) {
        super(client, session, timeLimit);
    }
}
