package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageStart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class GameRoomPlayOperator extends GameRoomPlayClient{
    // checker
    Timer endCheckTimer;
    TimerTask endCheckTimerTask;
    // time limit
    private final Duration timeLimit;
    private LocalDateTime endTime;

    public GameRoomPlayOperator(ThisApplication thisApplication, Duration timeLimit) {
        super(thisApplication);
        this.timeLimit = timeLimit;
    }

    @Override
    public void onGameStart(GameMessageStart gameMessageStart) {
        super.onGameStart(gameMessageStart);
        endCheckTimer = new Timer();
        endCheckTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isEnd()) {
                    declareGameEnd();
                }
            }
        };
        endCheckTimer.schedule(endCheckTimerTask,0,1000);
    }

    @Override
    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        super.onGameEnd(gameMessageEnd);
        endCheckTimerTask.cancel();
        endCheckTimer.cancel();
        endCheckTimerTask = null;
        endCheckTimer = null;
    }

    public boolean isEnd(){
        if (gameStatus != GameStatus.GAME_READY) {
            return false;
        } else {
            return LocalDateTime.now().isAfter(endTime);
        }
    }
}
