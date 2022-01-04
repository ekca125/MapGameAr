package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.GameEndMessage;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class GameRoomClient extends GameRoomLinker {
    private RoomStatus roomStatus;

    public GameRoomClient(@NonNull Client client,
                          @NonNull Session session,
                          @NonNull SocketClient socketClient) throws ExecutionException, InterruptedException {
        super(client, session, socketClient);
        roomStatus = RoomStatus.READY;
    }

    public boolean isEnd() {
        return roomStatus == RoomStatus.END;
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
