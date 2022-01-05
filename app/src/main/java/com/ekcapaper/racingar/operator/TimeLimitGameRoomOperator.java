package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.network.GameStartMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeLimitGameRoomOperator extends GameRoomOperator {
    private final Duration timeLimit;
    private LocalDateTime endTime;

    public TimeLimitGameRoomOperator(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
        this.endTime = null;
    }

    @Override
    public void onGameStart(GameStartMessage gameStartMessage) {
        super.onGameStart(gameStartMessage);
        endTime = LocalDateTime.now().plusSeconds(timeLimit.getSeconds());
    }

    @Override
    boolean isEnd() {
        if(getRoomStatus() != RoomStatus.GAME_STARTED){
            return false;
        }
        else{
            return LocalDateTime.now().isAfter(endTime);
        }
    }
}
