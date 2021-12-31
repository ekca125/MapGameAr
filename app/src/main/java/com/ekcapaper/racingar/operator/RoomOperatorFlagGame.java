package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import lombok.Builder;

public class RoomOperatorFlagGame extends RoomOperator{
    private final List<GameFlag> gameFlagList;
    private final int timeLimitSecond;
    private LocalTime startTime;
    private LocalTime endTime;

    @Builder
    public RoomOperatorFlagGame(Session session, SocketClient socketClient, Match match, List<GameFlag> gameFlagList, int timeLimitSecond) {
        super(session, socketClient, match);
        this.gameFlagList = gameFlagList;
        this.timeLimitSecond = timeLimitSecond;
    }

    @Override
    public void startGame() {
        startTime = LocalTime.now();
    }

    @Override
    protected boolean isEnd() {
        if(timeLimitSecond == 0){
            // 끝나지 않음
            return false;
        }
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
