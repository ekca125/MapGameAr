package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import lombok.Setter;

public class RoomOperatorFlagGame extends RoomOperator{
    private final List<GameFlag> gameFlagList;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public RoomOperatorFlagGame(Client client, Session session) throws ExecutionException, InterruptedException {
        super(client, session);
        gameFlagList = new ArrayList<>();
    }

    @Override
    void onGameStart(GameStartMessage gameStartMessage) {
        super.onGameStart(gameStartMessage);
        int limitTimeSecond = gameStartMessage.getLimitTimeSecond();
        startDateTime = LocalDateTime.now();
        if(limitTimeSecond != 0) {
            endDateTime = startDateTime.plusSeconds(limitTimeSecond);
        }
        else{
            endDateTime = null;
        }
    }


    @Override
    protected boolean isEnd() {
        long unownedFlagCount = gameFlagList.stream().filter(gameFlag -> !gameFlag.isOwned()).count();
        if(unownedFlagCount == 0){
            return true;
        }
        if(endDateTime == null){
            return true;
        }
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        if(currentLocalDateTime.isAfter(endDateTime)){
            return true;
        }
        return false;
    }

    @Override
    protected boolean isVictory() {
        return false;
    }

    @Override
    protected void victorySequence() {

    }

    @Override
    protected boolean isDefeat() {
        return false;
    }

    @Override
    protected void defeatSequence() {

    }

    @Override
    protected void defaultSequence() {

    }
}
