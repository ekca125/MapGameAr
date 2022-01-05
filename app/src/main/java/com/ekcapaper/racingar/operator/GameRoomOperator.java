package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.network.GameEndMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class GameRoomOperator extends GameRoomClient {
    Timer endCheckTimer;
    TimerTask endCheckTimerTask;

    public GameRoomOperator(Client client, Session session) {
        super(client, session);
        this.endCheckTimer = new Timer();
        this.endCheckTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isEnd()) {
                    declareGameEnd();
                }
            }
        };
        this.endCheckTimer.schedule(endCheckTimerTask,
                TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS),
                TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS)
        );
    }

    @Override
    public void onGameEnd(GameEndMessage gameEndMessage) {
        super.onGameEnd(gameEndMessage);
        endCheckTimerTask.cancel();
        endCheckTimer.cancel();
    }

    abstract boolean isEnd();
}
