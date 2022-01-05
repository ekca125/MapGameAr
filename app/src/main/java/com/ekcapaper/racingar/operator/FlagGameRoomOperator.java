package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;

public class FlagGameRoomOperator extends TimeLimitGameRoomOperator {
    private final List<GameFlag> gameFlagList;
    public FlagGameRoomOperator(Client client, Session session, Duration timeLimit, List<GameFlag> gameFlagList) {
        super(client, session, timeLimit);
        this.gameFlagList = gameFlagList;
    }
}
