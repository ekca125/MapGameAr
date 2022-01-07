package com.ekcapaper.racingar.operator.maker.newroom;

import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.TimeLimitGameRoomOperatorMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;

public class TimeLimitGameRoomOperatorNewMaker extends GameRoomOperatorNewMaker implements TimeLimitGameRoomOperatorMaker {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
    }

    @Override
    public TimeLimitGameRoomOperator makeTimeLimitGameRoomOperator() {
        TimeLimitGameRoomOperator timeLimitGameRoomOperator = new TimeLimitGameRoomOperator(client, session, timeLimit);
        timeLimitGameRoomOperator.createMatch();
        return timeLimitGameRoomOperator;
    }
}
