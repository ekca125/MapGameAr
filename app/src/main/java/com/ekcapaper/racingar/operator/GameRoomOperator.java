package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.operator.checker.EndChecker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GameRoomOperator extends GameRoomClient {
    Timer endCheckTimer;
    TimerTask endCheckTimerTask;
    EndChecker endChecker;

    public GameRoomOperator(Client client, Session session, EndChecker endChecker) {
        super(client, session);
        this.endChecker = endChecker;
        this.endCheckTimer = new Timer();
        this.endCheckTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (endChecker.isEnd()) {
                    sendMatchData(new GameEndMessage());
                }
            }
        };
        this.endCheckTimer.schedule(endCheckTimerTask,
                TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS),
                TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS)
        );
    }

}
