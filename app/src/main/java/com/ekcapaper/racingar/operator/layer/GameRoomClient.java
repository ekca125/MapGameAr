package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.ekcapaper.racingar.operator.data.RoomStatus;
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

import lombok.Getter;

public class GameRoomClient extends RoomClient {
    // 게임 플레이어
    private final Player currentPlayer;
    @Getter
    private List<Player> playerList;
    @Getter
    private RoomStatus roomStatus;

    public GameRoomClient(Client client, Session session) {
        super(client, session);
        this.currentPlayer = new Player(session.getUserId());

        this.playerList = new ArrayList<>();
        this.playerList.add(currentPlayer);

        this.roomStatus = RoomStatus.GAME_NOT_READY;
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

    // create or join
    @Override
    public boolean createMatch() {
        boolean success = super.createMatch();
        if (success) {
            changeRoomStatus(RoomStatus.GAME_READY);
        }
        return success;
    }

    @Override
    public boolean joinMatch(String matchId) {
        boolean success = super.joinMatch(matchId);
        if (success) {
            changeRoomStatus(RoomStatus.GAME_READY);
        }
        return success;
    }

    // match presence
    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            List<Player> joinPlayerList = joinList.stream()
                    .map((userPresence) -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            playerList.addAll(joinPlayerList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            List<Player> leavePlayerList = leaveList.stream()
                    .map((userPresence -> new Player(userPresence.getUserId())))
                    .collect(Collectors.toList());
            playerList.removeAll(leavePlayerList);
        }
        playerList = playerList.stream().distinct().collect(Collectors.toList());
    }

    // match event
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

    public void declareGameStart() {
        sendMatchData(new GameStartMessage());
    }

    public void onGameStart(GameStartMessage gameStartMessage) {
        changeRoomStatus(RoomStatus.GAME_STARTED);
    }

    public void declareCurrentPlayerMove(Location location) {
        MovePlayerMessage movePlayerMessage = new MovePlayerMessage(currentPlayer.getUserId(), location.getLatitude(), location.getLongitude());
        sendMatchData(movePlayerMessage);
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

    public void declareGameEnd() {
        sendMatchData(new GameEndMessage());
    }

    public void onGameEnd(GameEndMessage gameEndMessage) {
        changeRoomStatus(RoomStatus.GAME_END);
    }

    private void changeRoomStatus(RoomStatus roomStatus) {
        // not ready -> ready -> started -> end
        if (this.roomStatus == RoomStatus.GAME_NOT_READY && roomStatus == RoomStatus.GAME_READY) {
            this.roomStatus = roomStatus;
        } else if (this.roomStatus == RoomStatus.GAME_READY && roomStatus == RoomStatus.GAME_STARTED) {
            this.roomStatus = roomStatus;
        } else if (this.roomStatus == RoomStatus.GAME_STARTED && roomStatus == RoomStatus.GAME_END) {
            this.roomStatus = roomStatus;
        } else {
            throw new IllegalStateException();
        }
    }
}
