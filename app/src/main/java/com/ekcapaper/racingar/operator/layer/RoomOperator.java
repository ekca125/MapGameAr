package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import kotlin.NotImplementedError;
import lombok.NonNull;

public class RoomOperator extends RoomHandler {
    private final Timer roomEndChecker;
    private final TimerTask roomEndCheckerTask;
    private final Duration timeLimit;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public RoomOperator(@NonNull Client client,
                        @NonNull Session session,
                        @NonNull SocketClient socketClient,
                        @NonNull Duration timeLimit) throws ExecutionException, InterruptedException {
        super(client, session, socketClient);
        roomEndChecker = new Timer();
        roomEndCheckerTask = new TimerTask() {
            @Override
            public void run() {
                if (isEnd()) {
                    sendMatchData(new GameEndMessage());
                }
            }
        };
        this.timeLimit = timeLimit;
    }

    @Override
    public boolean isEnd() {
        if (super.isEnd()) {
            return true;
        } else if (timeLimit.isZero()) {
            return false;
        } else {
            LocalDateTime currentLocalDateTime = LocalDateTime.now();
            return currentLocalDateTime.isAfter(endDateTime);
        }
    }

    @Override
    public void onGameStart(GameStartMessage gameStartMessage) {
        super.onGameStart(gameStartMessage);
        startDateTime = LocalDateTime.now();
        endDateTime = startDateTime.plusSeconds(timeLimit.getSeconds());
    }

    @Override
    public void onGameEnd(GameEndMessage gameEndMessage) {
        super.onGameEnd(gameEndMessage);
        roomEndChecker.cancel();
        if (isVictory()) {
            onVictory();
        } else if (isDefeat()) {
            onDefeat();
        } else {
            onDefault();
        }
    }

    // victory
    protected boolean isVictory() {
        throw new NotImplementedError();
    }

    protected void onVictory() {
        throw new NotImplementedError();
    }

    // defeat
    protected boolean isDefeat() {
        throw new NotImplementedError();
    }

    protected void onDefeat() {
        throw new NotImplementedError();
    }

    // default
    protected void onDefault() {
        throw new NotImplementedError();
    }
}
