package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

public class RoomOperatorFlagGame extends RoomOperator{
    // Factory 에서 생성하여 가져온다.
    // 이 객체의 생성에 Factory 를 사용하는 이유
    /*
        1. 맵을 만들때 REST API 를 이용해서 서버에서 받아온다.
        2. 방에 접속하는 경우에는 스토리지 엔진에서 작성된 것을 가져온다.

        각각의 모드마다 createMatch 와 StorageEngine 에서 요구하는 것이 다르기 때문에 생성부분을 팩토리로 처리하는 것이 액티비티를 고려했을때에도 더 적합하다
    */
    private final List<GameFlag> gameFlagList;

    public RoomOperatorFlagGame(Client client, Session session, List<GameFlag> gameFlagList) throws ExecutionException, InterruptedException, IllegalStateException{
        super(client, session);
        this.gameFlagList = gameFlagList;
        if(this.gameFlagList == null || this.gameFlagList.size()<=0){
            throw new IllegalStateException();
        }
    }

    @Override
    void onGameStart(GameStartMessage gameStartMessage) {
        super.onGameStart(gameStartMessage);
    }


    @Override
    void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        super.onMovePlayer(movePlayerMessage);
        for(GameFlag gameFlag:gameFlagList){
            gameFlag.reflectPlayerLocation(movePlayerMessage.getLocation(), movePlayerMessage.getUserId());
        }
    }

    @Override
    protected boolean isEnd() {
        long unownedFlagCount = gameFlagList.stream().filter(gameFlag -> !gameFlag.isOwned()).count();
        boolean end = super.isEnd();
        if(end){
            return true;
        }
        else if(unownedFlagCount == 0){
            return true;
        }
        return false;
    }

    @Override
    protected boolean isVictory() {
        Map<String, Long> gameFlagCountMap = gameFlagList.stream()
                .filter(GameFlag::isOwned)
                .collect(Collectors.groupingBy(GameFlag::getUserId,Collectors.counting()));
        OptionalLong gameFlagCountMaxOptional = gameFlagCountMap.values().stream()
                .mapToLong(Long::longValue)
                .max();
        try {
            if (gameFlagCountMaxOptional.isPresent()) {
                if (gameFlagCountMap.containsKey(getCurrentUserId())) {
                    long ownGameFlagCount = gameFlagCountMap.get(getCurrentUserId());
                    long gameFlagCountMax = gameFlagCountMaxOptional.getAsLong();
                    if(gameFlagCountMax <= ownGameFlagCount){
                        return true;
                    }
                }
            }
        }
        catch (NullPointerException nullPointerException){
            return false;
        }
        return false;
    }

    @Override
    protected void victorySequence() {

    }

    @Override
    protected boolean isDefeat() {
        return !isVictory();
    }

    @Override
    protected void defeatSequence() {

    }

    @Override
    protected void defaultSequence() {

    }
}
