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
import java.util.concurrent.ExecutionException;

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
        if (super.isEnd()) {
            return true;
        } else {
            long unownedFlagCount = gameFlagList.stream().filter(gameFlag -> !gameFlag.isOwned()).count();
            return unownedFlagCount == 0;
        }
    }
}
