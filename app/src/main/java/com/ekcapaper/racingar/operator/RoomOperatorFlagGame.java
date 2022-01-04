package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.MovePlayerMessage;
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

import lombok.Builder;
import lombok.NonNull;

public class RoomOperatorFlagGame extends RoomOperator {
    private final List<GameFlag> gameFlagList;

    public RoomOperatorFlagGame(@NonNull Client client,
                                @NonNull Session session,
                                @NonNull SocketClient socketClient,
                                @NonNull Match match,
                                @NonNull Channel chatChannel,
                                @NonNull Duration timeLimit,
                                @NonNull List<GameFlag> gameFlagList) throws ExecutionException, InterruptedException {
        super(client, session, socketClient, match, chatChannel, timeLimit);
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
        long unownedFlagCount = gameFlagList.stream().filter(gameFlag -> !gameFlag.isOwned()).count();
        boolean end = super.isEnd();
        if (end) {
            return true;
        } else return unownedFlagCount == 0;
    }

    @Override
    protected boolean isVictory() {
        Map<String, Long> gameFlagCountMap = gameFlagList.stream()
                .filter(GameFlag::isOwned)
                .collect(Collectors.groupingBy(GameFlag::getUserId, Collectors.counting()));
        OptionalLong gameFlagCountMaxOptional = gameFlagCountMap.values().stream()
                .mapToLong(Long::longValue)
                .max();
        try {
            if (gameFlagCountMaxOptional.isPresent()) {
                if (gameFlagCountMap.containsKey(getCurrentUserId())) {
                    long ownGameFlagCount = gameFlagCountMap.get(getCurrentUserId());
                    long gameFlagCountMax = gameFlagCountMaxOptional.getAsLong();
                    if (gameFlagCountMax <= ownGameFlagCount) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
        return false;
    }

}
