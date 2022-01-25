package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;

import java.util.List;

public class FlagGameRoomClient extends GameRoomClient{
    private List<GameFlag> gameFlagList;
    public FlagGameRoomClient(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);
        

    }

    /*


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
*/
}
