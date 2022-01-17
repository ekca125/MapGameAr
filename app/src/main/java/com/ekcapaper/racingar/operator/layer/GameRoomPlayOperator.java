package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GameRoomPlayOperator extends GameRoomPlayClient {
    // checker
    Timer endCheckTimer;
    TimerTask endCheckTimerTask;
    // time limit
    private final Duration timeLimit;
    private LocalDateTime endTime;

    public GameRoomPlayOperator(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
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
                TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS),
                TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)
        );
    }

    @Override
    public void onGameStart(GameMessageStart gameMessageStart) {
        super.onGameStart(gameMessageStart);
        endTime = LocalDateTime.now().plusSeconds(timeLimit.getSeconds());
    }

    @Override
    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        super.onGameEnd(gameMessageEnd);
        endCheckTimerTask.cancel();
        endCheckTimer.cancel();
    }

    public boolean isEnd() {
        if (this.getGameStatus() != GameStatus.GAME_STARTED) {
            return false;
        } else {
            return LocalDateTime.now().isAfter(endTime);
        }
    }
}
