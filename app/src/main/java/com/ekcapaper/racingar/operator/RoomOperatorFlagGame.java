package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.util.List;
import java.util.Optional;

import lombok.Builder;

public class RoomOperatorFlagGame extends RoomOperator{
    List<GameFlag> gameFlagList;

    @Builder
    public RoomOperatorFlagGame(Session session, SocketClient socketClient, Match match, List<GameFlag> gameFlagList) {
        super(session, socketClient, match);
        this.gameFlagList = gameFlagList;
    }

    @Override
    protected boolean isEnd() {
        return false;
    }

    @Override
    protected boolean isVictory() {
        return false;
    }

    @Override
    protected boolean isDefeat() {
        return false;
    }

    @Override
    protected void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        super.onMovePlayer(movePlayerMessage);
        // 플레이어가 이동된 상태
        // 깃발을 소유했는지 여부를 확인
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
        optionalPlayer.ifPresent((player -> {
            player.getLocation().ifPresent(location -> {
                gameFlagList.forEach((gameFlag -> {
                    gameFlag.reflectPlayerLocation(location, player.getUserId());
                }));
            });
        }));
    }
}
