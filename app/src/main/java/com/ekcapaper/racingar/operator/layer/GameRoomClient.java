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
import com.heroiclabs.nakama.AbstractSocketListener;
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
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.NotificationList;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class GameRoomClient implements SocketListener {
    // nakama 서버와의 연동을 진행하는 클래스
    NakamaNetworkManager nakamaNetworkManager;
    // nakama 서버와 연동된 정보
    private Match match;
    private List<UserPresence> channelUserPresenceList;
    private List<UserPresence> matchUserPresenceList;
    // 게임 상태
    @Getter
    GameStatus gameStatus;
    // 게임을 진행하는 플레이어의 리스트
    @Getter
    List<Player> playerList;
    // 메시지를 처리한 후에 진행될 내용
    @Setter
    Runnable afterGameStartMessage;
    @Setter
    Runnable afterMovePlayerMessage;
    @Setter
    Runnable afterGameEndMessage;

    public GameRoomClient(NakamaNetworkManager nakamaNetworkManager){
        // nakama 서버와의 연동을 진행하는 클래스
        this.nakamaNetworkManager = nakamaNetworkManager;
        this.matchUserPresenceList = new ArrayList<>();
        this.channelUserPresenceList = new ArrayList<>();
        // nakama 서버와 연동된 정보
        this.match = null;
        // 게임 정보
        gameStatus = GameStatus.GAME_NOT_INIT;
        playerList = new ArrayList<>();
        // after callback
        afterGameStartMessage = ()->{};
        afterMovePlayerMessage = () -> {};
        afterGameEndMessage = () -> {};
    }

    public boolean createMatch(){
        match = nakamaNetworkManager.createMatchSync(this);
        return match != null;
    }

    public boolean joinMatch(String matchId){
        match = nakamaNetworkManager.joinMatchSync(this, matchId);
        onMatchJoinPresence(match.getPresences());
        return match != null;
    }

    public final void sendMatchData(GameMessage gameMessage) {
        nakamaNetworkManager.sendMatchData(match.getMatchId(),gameMessage);
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
        afterGameEnd.run();
    }
}
