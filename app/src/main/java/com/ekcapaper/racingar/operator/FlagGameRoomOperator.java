package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlagGameRoomOperator extends TimeLimitGameRoomOperator {
    private final List<GameFlag> gameFlagList;
    public FlagGameRoomOperator(Client client, Session session, Duration timeLimit, List<GameFlag> gameFlagList) {
        super(client, session, timeLimit);
        this.gameFlagList = gameFlagList;
    }

    @Override
    boolean isEnd() {
        if(super.isEnd()){
            return true;
        }
        else if(getUnownedFlagList().size() <= 0){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        super.onMovePlayer(movePlayerMessage);
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
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

    public List<GameFlag> getUnownedFlagList(){
        return gameFlagList.stream()
                .filter(gameFlag -> !gameFlag.isOwned())
                .collect(Collectors.toList());
    }
}
