package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.make.TimeLimitGameRoomOperatorMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;

public class TimeLimitGameRoomOperatorJoinMaker extends GameRoomOperatorJoinMaker implements TimeLimitGameRoomOperatorMaker {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorJoinMaker(Client client, Session session, String matchId, Duration timeLimit) {
        super(client, session, matchId);
        this.timeLimit = timeLimit;
    }

    @Override
    public TimeLimitGameRoomOperator makeTimeLimitGameRoomOperator() {
        TimeLimitGameRoomOperator timeLimitGameRoomOperator = new TimeLimitGameRoomOperator(client, session, timeLimit);
        timeLimitGameRoomOperator.joinMatch(matchId);
        return timeLimitGameRoomOperator;
    }
}
