package com.ekcapaper.racingar.operator.layer;

import android.location.Location;

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
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GameRoomPlayClient extends GameRoomClient{
    GameStatus gameStatus;
    List<Player> playerList;

    public GameRoomPlayClient(ThisApplication thisApplication) {
        super(thisApplication);
        gameStatus = GameStatus.GAME_READY;
        playerList = null;
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

    public Optional<Player> getPlayerOptional(String userId){
        return Optional.ofNullable(getPlayer(userId));
    }

    public Player getCurrentPlayer(){
        String userId = thisApplication.getCurrentUserId();
        return getPlayer(userId);
    }

    private void changeRoomStatus(GameStatus gameStatus) {
        // ready -> running -> end
        if (this.gameStatus == GameStatus.GAME_READY && gameStatus == GameStatus.GAME_RUNNING) {
            this.gameStatus = gameStatus;
        } else if (this.gameStatus == GameStatus.GAME_RUNNING && gameStatus == GameStatus.GAME_END) {
            this.gameStatus = gameStatus;
        }
    }

    public final void sendMatchData(GameMessage gameMessage) {
        SocketClient socketClient = thisApplication.getSocketClient();
        String matchId = thisApplication.getCurrentMatchId();
        socketClient.sendMatchData(
                matchId,
                gameMessage.getOpCode().ordinal(),
                gameMessage.getPayload().getBytes(StandardCharsets.UTF_8)
        );
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
            GroupUserList groupUserList = thisApplication.getCurrentGroupUserList();
            playerList = groupUserList.getGroupUsersList()
                    .stream()
                    .map(groupUser -> new Player(groupUser.getUser().getId()))
                    .collect(Collectors.toList());
            changeRoomStatus(GameStatus.GAME_RUNNING);
        } catch (NullPointerException ignored){

        }
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
    }

    public void declareGameEnd() {
        GameMessageEnd gameMessageEnd = new GameMessageEnd();
        sendMatchData(gameMessageEnd);
        onGameEnd(gameMessageEnd);
    }

    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        changeRoomStatus(GameStatus.GAME_END);
    }

}
