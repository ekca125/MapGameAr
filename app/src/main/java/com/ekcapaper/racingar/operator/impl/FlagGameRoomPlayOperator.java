package com.ekcapaper.racingar.operator.impl;

import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlagGameRoomPlayOperator extends GameRoomPlayOperator {
    private final List<GameFlag> gameFlagList;

    public FlagGameRoomPlayOperator(ThisApplication thisApplication, Duration timeLimit, List<GameFlag> gameFlagList) {
        super(thisApplication,timeLimit);
        this.gameFlagList = gameFlagList;
    }

    @Override
    public boolean isEnd() {
        if (super.isEnd()) {
            return true;
        } else return getUnownedFlagList().size() <= 0;
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
