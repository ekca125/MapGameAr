package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameStartMessage;
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

public abstract class RoomOperatorFlagGame extends RoomOperator{
    private final List<GameFlag> gameFlagList;

    public RoomOperatorFlagGame(Client client, Session session) throws ExecutionException, InterruptedException {
        super(client, session);
        gameFlagList = new ArrayList<>();
    }

    @Override
    void onGameStart(GameStartMessage gameStartMessage) {
        super.onGameStart(gameStartMessage);
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
    protected boolean isDefeat() {
        return !isVictory();
    }
}
