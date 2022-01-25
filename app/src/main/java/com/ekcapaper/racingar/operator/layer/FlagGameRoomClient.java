package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;

import java.util.List;
import java.util.stream.Collectors;

public class FlagGameRoomClient extends GameRoomClient{
    private List<GameFlag> gameFlagList;
    public FlagGameRoomClient(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);
        gamePlayerList.stream()
                .filter(player -> gameMessageMovePlayer.getUserId().equals(player.getUserId()))
                .forEach(player -> {
                    player.getLocation().ifPresent((Location location) -> {
                        gameFlagList.stream().forEach((GameFlag gameFlag) -> {
                            gameFlag.reflectPlayerLocation(location, player.getUserId());
                        });
                    });
                });
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
