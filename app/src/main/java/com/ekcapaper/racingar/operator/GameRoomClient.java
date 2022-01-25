package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.network.GameMessage;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.network.GameMessageOpCode;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.google.gson.Gson;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class GameRoomClient implements SocketListener {
    // nakama 서버와의 연동을 진행하는 클래스
    NakamaNetworkManager nakamaNetworkManager;
    // 게임 상태
    @Getter
    GameStatus currentGameStatus;
    // 게임을 진행하는 플레이어의 리스트
    @Getter
    List<Player> gamePlayerList;
    // 메시지를 처리한 후에 진행될 내용
    @Setter
    Runnable afterGameStartMessage;
    @Setter
    Runnable afterMovePlayerMessage;
    @Setter
    Runnable afterGameEndMessage;
    // nakama 서버와 연동된 정보
    private Match match;
    private final List<UserPresence> channelUserPresenceList;
    private final List<UserPresence> matchUserPresenceList;

    public GameRoomClient(NakamaNetworkManager nakamaNetworkManager) {
        // nakama 서버와의 연동을 진행하는 클래스
        this.nakamaNetworkManager = nakamaNetworkManager;
        this.matchUserPresenceList = new ArrayList<>();
        this.channelUserPresenceList = new ArrayList<>();
        // nakama 서버와 연동된 정보
        this.match = null;
        // 게임 정보
        currentGameStatus = GameStatus.GAME_NOT_INIT;
        gamePlayerList = new ArrayList<>();
        // after callback
        afterGameStartMessage = () -> {
        };
        afterMovePlayerMessage = () -> {
        };
        afterGameEndMessage = () -> {
        };
    }

    public boolean createMatch() {
        match = nakamaNetworkManager.createMatchSync(this);
        if (match == null) {
            return false;
        } else {
            goGameStatus(GameStatus.GAME_READY);
            return true;
        }
    }

    public boolean joinMatch(String matchId) {
        match = nakamaNetworkManager.joinMatchSync(this, matchId);
        onMatchJoinPresence(match.getPresences());
        if (match == null) {
            return false;
        } else {
            goGameStatus(GameStatus.GAME_READY);
            return true;
        }
    }

    private boolean goGameStatus(GameStatus goGameStatus) {
        // not init -> ready -> running -> end
        if (GameStatus.GAME_NOT_INIT == currentGameStatus && GameStatus.GAME_READY == goGameStatus) {
            // not init -> ready
            this.currentGameStatus = GameStatus.GAME_READY;
            return true;
        } else if (GameStatus.GAME_READY == currentGameStatus && GameStatus.GAME_RUNNING == goGameStatus) {
            // ready -> running
            this.currentGameStatus = GameStatus.GAME_RUNNING;
            return true;
        } else if (GameStatus.GAME_RUNNING == currentGameStatus && GameStatus.GAME_END == goGameStatus) {
            // running -> end
            this.currentGameStatus = GameStatus.GAME_END;
            return true;
        } else {
            return false;
        }
    }

    public final void sendMatchData(GameMessage gameMessage) {
        nakamaNetworkManager.sendMatchData(match.getMatchId(), gameMessage);
    }

    @Override
    public void onDisconnect(Throwable t) {

    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {

    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        // join 처리
        List<UserPresence> joinList = presence.getJoins();
        if (joinList != null) {
            onChannelJoinPresence(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = presence.getLeaves();
        if (leaveList != null) {
            onChannelLeavePresence(leaveList);
        }
    }

    public void onChannelJoinPresence(List<UserPresence> joinList) {
        channelUserPresenceList.addAll(joinList);
    }

    public void onChannelLeavePresence(List<UserPresence> leaveList) {
        channelUserPresenceList.removeAll(leaveList);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {

    }


    @Override
    public void onMatchData(MatchData matchData) {
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

    // game event
    public void declareGameStart() {
        if (currentGameStatus != GameStatus.GAME_READY) {
            // ready 상태에서만 시작을 선언할 수 있다.
            throw new IllegalStateException();
        }
        GameMessageStart gameMessageStart = new GameMessageStart();
        sendMatchData(gameMessageStart);
        onGameStart(gameMessageStart);
    }

    public void onGameStart(GameMessageStart gameMessageStart) {
        if (currentGameStatus == GameStatus.GAME_READY) {
            // ready 상태에서만 메시지를 처리한다.
            List<Player> matchPlayers = matchUserPresenceList.stream()
                    .map(userPresence -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            gamePlayerList.addAll(matchPlayers);
            goGameStatus(GameStatus.GAME_RUNNING);
            afterGameStartMessage.run();
        }
    }

    public void declareCurrentPlayerMove(Location location) {
        if (currentGameStatus != GameStatus.GAME_RUNNING) {
            // running 상태에서만 이동을 선언할 수 있다.
            throw new IllegalStateException();
        }
        GameMessageMovePlayer gameMessageMovePlayer = new GameMessageMovePlayer(
                nakamaNetworkManager.getCurrentSessionUserId(),
                location.getLatitude(),
                location.getLongitude()
        );
        sendMatchData(gameMessageMovePlayer);
        onMovePlayer(gameMessageMovePlayer);
    }

    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        if (currentGameStatus == GameStatus.GAME_RUNNING) {
            // running 상태에서만 메시지를 처리한다.
            gamePlayerList.stream()
                    .filter(player -> player.getUserId().equals(gameMessageMovePlayer.getUserId()))
                    .forEach(player -> {
                        Location location = new Location("");
                        location.setLatitude(gameMessageMovePlayer.getLatitude());
                        location.setLongitude(gameMessageMovePlayer.getLongitude());
                        player.updateLocation(location);
                    });
            afterMovePlayerMessage.run();
        }
    }

    public void declareGameEnd() {
        if (currentGameStatus != GameStatus.GAME_RUNNING) {
            // running 상태에서만 종료를 선언할 수 있다.
            throw new IllegalStateException();
        }
        GameMessageEnd gameMessageEnd = new GameMessageEnd();
        sendMatchData(gameMessageEnd);
        onGameEnd(gameMessageEnd);
    }

    public void onGameEnd(GameMessageEnd gameMessageEnd) {
        if (currentGameStatus == GameStatus.GAME_RUNNING) {
            // running 상태에서만 메시지를 처리한다.
            goGameStatus(GameStatus.GAME_END);
            afterGameEndMessage.run();
        }
    }
    //

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            onMatchJoinPresence(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            onMatchLeavePresence(leaveList);
        }
    }

    public void onMatchJoinPresence(List<UserPresence> joinList) {
        matchUserPresenceList.addAll(joinList);
    }

    public void onMatchLeavePresence(List<UserPresence> leaveList) {
        matchUserPresenceList.removeAll(leaveList);
    }

    @Override
    public void onNotifications(NotificationList notifications) {

    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {

    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {

    }

    @Override
    public void onStreamData(StreamData data) {

    }
}
