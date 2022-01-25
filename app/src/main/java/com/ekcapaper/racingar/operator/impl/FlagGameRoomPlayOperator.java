package com.ekcapaper.racingar.operator.impl;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayClient;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlagGameRoomPlayOperator extends GameRoomPlayClient {
    private List<GameFlag> gameFlagList;

    public FlagGameRoomPlayOperator(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);
        Optional<Player> optionalPlayer = getPlayerOptional(gameMessageMovePlayer.getUserId());
        optionalPlayer.ifPresent((player -> {
            player.getLocation().ifPresent(location -> {
                gameFlagList.forEach((gameFlag -> {
                    gameFlag.reflectPlayerLocation(location, player.getUserId());
                }));
            });
        }));
    }

    public int getPoint(String userId) {
        return (int) gameFlagList.stream()
                .filter(gameFlag -> gameFlag.getUserId().equals(userId))
                .count();
    }

    public List<GameFlag> getUnownedFlagList() {
        return gameFlagList.stream()
                .filter(gameFlag -> !gameFlag.isOwned())
                .collect(Collectors.toList());
    }
}
