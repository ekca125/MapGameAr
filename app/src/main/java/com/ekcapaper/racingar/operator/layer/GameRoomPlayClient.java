package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.network.GameMessageOpCode;
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

public class GameRoomPlayClient extends GameRoomClient {
    @Getter
    private final Player currentPlayer;
    @Getter
    private List<Player> playerList;
    @Getter
    private GameStatus gameStatus;

    public GameRoomPlayClient(Client client, Session session) {
        super(client, session);
        this.currentPlayer = new Player(session.getUserId());

        this.playerList = new ArrayList<>();
        this.playerList.add(currentPlayer);

        this.gameStatus = GameStatus.GAME_NOT_READY;
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
            changeRoomStatus(GameStatus.GAME_READY);
        }
        return success;
    }

    @Override
    public boolean joinMatch(String matchId) {
        boolean success = super.joinMatch(matchId);
        if (success) {
            changeRoomStatus(GameStatus.GAME_READY);
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

        GameMessageOpCode gameMessageOpCode = GameMessageOpCode.values()[(int) networkOpCode];
        String data = new String(networkBytes, StandardCharsets.UTF_8);
        switch (gameMessageOpCode) {
            case MOVE_PLAYER:
                GameMessageMovePlayer gameMessageMovePlayer = gson.fromJson(data, GameMessageMovePlayer.class);
                onMovePlayer(gameMessageMovePlayer);
                break;
            case GAME_START:
                GameMessageStart gameMessageStart = gson.fromJson(data, GameMessageStart.class);
                onGameStart(gameMessageStart);
                break;
            case GAME_END:
                GameMessageEnd gameMessageEnd = gson.fromJson(data, GameMessageEnd.class);
                onGameEnd(gameMessageEnd);
                break;
            default:
                break;
        }
    }

    public void declareGameStart() {
        GameMessageStart gameMessageStart = new GameMessageStart();
        sendMatchData(gameMessageStart);
        onGameStart(gameMessageStart);
    }

    public void onGameStart(GameMessageStart gameMessageStart) {
        changeRoomStatus(GameStatus.GAME_STARTED);
    }

    public void declareCurrentPlayerMove(Location location) {
        GameMessageMovePlayer gameMessageMovePlayer = new GameMessageMovePlayer(currentPlayer.getUserId(), location.getLatitude(), location.getLongitude());
        sendMatchData(gameMessageMovePlayer);
        onMovePlayer(gameMessageMovePlayer);
    }

    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        Optional<Player> optionalPlayer = getPlayer(gameMessageMovePlayer.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(gameMessageMovePlayer.getLatitude());
            location.setLongitude(gameMessageMovePlayer.getLongitude());
            player.updateLocation(location);
        }));
    }

    public void declareGameEnd() {
        GameMessageEnd gameMessageEnd = new GameMessageEnd();
        sendMatchData(gameMessageEnd);
        onGameEnd(gameMessageEnd);
    }

    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        changeRoomStatus(GameStatus.GAME_END);
    }

    private void changeRoomStatus(GameStatus gameStatus) {
        // not ready -> ready -> started -> end
        if (this.gameStatus == GameStatus.GAME_NOT_READY && gameStatus == GameStatus.GAME_READY) {
            this.gameStatus = gameStatus;
        } else if (this.gameStatus == GameStatus.GAME_READY && gameStatus == GameStatus.GAME_STARTED) {
            this.gameStatus = gameStatus;
        } else if (this.gameStatus == GameStatus.GAME_STARTED && gameStatus == GameStatus.GAME_END) {
            this.gameStatus = gameStatus;
        } else {
            throw new IllegalStateException();
        }
    }
}
