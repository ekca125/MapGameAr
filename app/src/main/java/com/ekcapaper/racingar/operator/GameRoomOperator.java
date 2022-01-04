package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.network.GameEndMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GameRoomOperator extends GameRoomClient{
    Timer endCheckTimer;
    TimerTask endCheckTimerTask;

    public GameRoomOperator(Client client, Session session) {
        super(client, session);
        endCheckTimer = new Timer();
        endCheckTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(isEnd()){
                    sendMatchData(new GameEndMessage());
                }
            }
        };
        endCheckTimer.schedule(endCheckTimerTask,
                TimeUnit.SECONDS.convert(1,TimeUnit.MILLISECONDS),
                TimeUnit.SECONDS.convert(1,TimeUnit.MILLISECONDS)
        );
    }

    public boolean isEnd(){

    }

    // end를 상위클래스에서 재정의
}
