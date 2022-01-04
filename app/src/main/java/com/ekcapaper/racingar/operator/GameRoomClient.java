package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.Message;
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

public class GameRoomClient extends RoomClient {
    // 게임 플레이어
    private final List<Player> playerList;
    private RoomStatus roomStatus;

    public GameRoomClient(Client client, Session session) {
        super(client, session);
        this.playerList = new ArrayList<>();
        this.playerList.add(new Player(session.getUserId()));
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
        if(success){
            roomStatus = RoomStatus.GAME_READY;
        }
        return success;
    }

    @Override
    public boolean joinMatch(String matchId) {
        boolean success = super.joinMatch(matchId);
        if(success){
            roomStatus = RoomStatus.GAME_READY;
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

    public void sendGameStartMessage(){
        sendMatchData(new GameStartMessage());
    }

    public void onGameStart(GameStartMessage gameStartMessage) {
        roomStatus = RoomStatus.GAME_STARTED;
    }

    public void sendMovePlayerMessage(String userId, Location location){
        MovePlayerMessage movePlayerMessage = new MovePlayerMessage(userId, location.getLatitude(), location.getLongitude());
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

    public void sendGameEndMessage(){
        sendMatchData(new GameEndMessage());
    }

    public void onGameEnd(GameEndMessage gameEndMessage) {
        roomStatus = RoomStatus.GAME_END;
    }

    private enum RoomStatus {
        GAME_NOT_READY,
        GAME_READY,
        GAME_STARTED,
        GAME_END
    }
}
