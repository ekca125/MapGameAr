package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.UserPresence;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameRoomClient extends RoomClient{
    // 게임 플레이어
    private final List<Player> playerList;
    private RoomStatus roomStatus;

    public GameRoomClient(Client client, Session session) {
        super(client, session);
        this.playerList = new ArrayList<>();
        this.playerList.add(new Player(session.getUserId()));

        this.roomStatus = RoomStatus.READY;
    }

    public Optional<Player> getPlayer(String userId) {
        try {
            Player goalPlayer = playerList.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .collect(Collectors.toList()).get(0);
            return Optional.ofNullable(goalPlayer);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            List<Player> joinPlayerList = joinList.stream()
                    .map((userPresence) -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            this.playerList.addAll(joinPlayerList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            List<Player> leavePlayerList = leaveList.stream()
                    .map((userPresence -> new Player(userPresence.getUserId())))
                    .collect(Collectors.toList());
            this.playerList.removeAll(leavePlayerList);
        }
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        Gson gson = new Gson();
        long networkOpCode = matchData.getOpCode();
        byte[] networkBytes = matchData.getData();

        OpCode opCode = OpCode.values()[(int) networkOpCode];
        String data = new String(networkBytes, StandardCharsets.UTF_8);
        switch (opCode) {
            case MOVE_PLAYER:
                MovePlayerMessage movePlayerMessage = gson.fromJson(data, MovePlayerMessage.class);
                onMovePlayer(movePlayerMessage);
                break;
            case GAME_START:
                GameStartMessage gameStartMessage = gson.fromJson(data, GameStartMessage.class);
                onGameStart(gameStartMessage);
                break;
            case GAME_END:
                GameEndMessage gameEndMessage = gson.fromJson(data, GameEndMessage.class);
                onGameEnd(gameEndMessage);
                break;
            default:
                break;
        }
    }


    public void onGameStart(GameStartMessage gameStartMessage) {
        roomStatus = RoomStatus.PROGRESS;
    }

    public void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(movePlayerMessage.getLatitude());
            location.setLongitude(movePlayerMessage.getLongitude());
            player.updateLocation(location);
        }));
    }

    public void onGameEnd(GameEndMessage gameEndMessage) {
        roomStatus = RoomStatus.END;
    }

    private enum RoomStatus {
        READY,
        PROGRESS,
        END
    }
}
