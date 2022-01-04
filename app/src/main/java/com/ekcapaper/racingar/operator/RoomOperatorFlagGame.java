package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.operator.layer.RoomOperator;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.NonNull;

public class RoomOperatorFlagGame extends RoomOperator {
    private final List<GameFlag> gameFlagList;

    public RoomOperatorFlagGame(@NonNull Client client,
                                @NonNull Session session,
                                @NonNull SocketClient socketClient,
                                @NonNull Duration timeLimit,
                                @NonNull List<GameFlag> gameFlagList) throws ExecutionException, InterruptedException {
        super(client, session, socketClient, timeLimit);
        this.gameFlagList = gameFlagList;
    }


    @Override
    public void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        super.onMovePlayer(movePlayerMessage);
        for (GameFlag gameFlag : gameFlagList) {
            gameFlag.reflectPlayerLocation(movePlayerMessage.getLocation(), movePlayerMessage.getUserId());
        }
    }

    @Override
    public boolean isEnd() {
        if (super.isEnd()) {
            return true;
        } else {
            long unownedFlagCount = gameFlagList.stream().filter(gameFlag -> !gameFlag.isOwned()).count();
            return unownedFlagCount == 0;
        }
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
                String currentPlayerUserId = getSession().getUserId();
                if (gameFlagCountMap.containsKey(currentPlayerUserId)) {
                    long ownGameFlagCount = gameFlagCountMap.get(currentPlayerUserId);
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
    protected void onVictory() {

    }

    @Override
    protected boolean isDefeat() {
        return !isVictory();
    }

    @Override
    protected void onDefeat() {

    }

    @Override
    protected void onDefault() {

    }
}
