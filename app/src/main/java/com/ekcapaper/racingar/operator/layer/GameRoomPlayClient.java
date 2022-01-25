package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.network.GameMessage;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.network.GameMessageOpCode;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class GameRoomPlayClient extends GameRoomClient{
    @Getter
    GameStatus gameStatus;
    @Getter
    List<Player> playerList;
    @Setter
    Runnable afterGameStartMessage;
    @Setter
    Runnable afterMovePlayer;
    @Setter
    Runnable afterGameEnd;
    public GameRoomPlayClient(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
        gameStatus = GameStatus.GAME_READY;
        playerList = new ArrayList<>();
        // after callback
        afterGameStartMessage = ()->{};
        afterMovePlayer = () -> {};
        afterGameEnd = () -> {};
    }


    private Player getPlayer(String userId){
        try{
            Player goalPlayer = playerList.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .collect(Collectors.toList())
                    .get(0);
            return goalPlayer;
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    
    // message 처리
    public final void sendMatchData(GameMessage gameMessage) {
        

        nakamaGameManager.sendGameRoomGameMessage(gameMessage);
    }

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
        try {
            GroupUserList groupUserList = nakamaGameManager.getGameRoomGroupUserList();
            playerList = groupUserList.getGroupUsersList()
                    .stream()
                    .map(groupUser -> new Player(groupUser.getUser().getId()))
                    .collect(Collectors.toList());
            changeRoomStatus(GameStatus.GAME_RUNNING);
        } catch (NullPointerException ignored){

        }
        afterGameStartMessage.run();
    }

    public void declareCurrentPlayerMove(Location location) {
        GameMessageMovePlayer gameMessageMovePlayer = new GameMessageMovePlayer(getCurrentPlayer().getUserId(), location.getLatitude(), location.getLongitude());
        sendMatchData(gameMessageMovePlayer);
        onMovePlayer(gameMessageMovePlayer);
    }

    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        Optional<Player> optionalPlayer = getPlayerOptional(gameMessageMovePlayer.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(gameMessageMovePlayer.getLatitude());
            location.setLongitude(gameMessageMovePlayer.getLongitude());
            player.updateLocation(location);
        }));
        afterMovePlayer.run();
    }

    public void declareGameEnd() {
        GameMessageEnd gameMessageEnd = new GameMessageEnd();
        sendMatchData(gameMessageEnd);
        onGameEnd(gameMessageEnd);
    }

    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        changeRoomStatus(GameStatus.GAME_END);
        afterGameEnd.run();;
    }

}
