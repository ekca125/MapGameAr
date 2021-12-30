package com.ekcapaper.racingar.operator;

import android.graphics.Path;
import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;
import com.heroiclabs.nakama.api.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Setter;

public abstract class RoomOperator extends AbstractSocketListener {
    // 서버에서의 유저들과 현재 방에서의 플레이어를 의미한다.
    private final List<UserPresence> userPresenceList;
    private final List<Player> playerList;
    // 채팅 데이터
    private final List<String> chattingLog;
    // 서버와의 연동
    private final Session session;
    private final SocketClient socketClient;
    private final Match match;
    // 종료조건을 확인하는 쓰레드
    private ScheduledExecutorService scheduledExecutorServiceEndCheck;
    // 액티비티나 다른 함수에서 이 클래스에서 작업을 마치고 이후에 처리할 내용을 정의한다.
    @Setter
    private Runnable victoryEndExecute;
    @Setter
    private Runnable defeatEndExecute;
    @Setter
    private Runnable basicEndExecute;
    // 유틸리티
    private final Gson gson = new Gson();
    
    public RoomOperator(Session session, SocketClient socketClient, Match match) {
        // 정보
        this.userPresenceList = new ArrayList<>();
        this.playerList = new ArrayList<>();
        this.chattingLog = new ArrayList<>();
        // 서버
        this.session = session;
        this.socketClient = socketClient;
        this.match = match;
        // 종료조건 콜백
        this.victoryEndExecute = () -> {
        };
        this.defeatEndExecute = () -> {
        };
        this.basicEndExecute = () -> {
        };
        // 서버와 연동
        socketClient.connect(session, this);
        // 종료조건 확인 시작
        scheduledExecutorServiceEndCheck = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorServiceEndCheck.scheduleWithFixedDelay(this::endCheck,1,1, TimeUnit.SECONDS);
    }

    final protected void endCheck() {
        // 종료 처리 확인 후에 패배 승리 확인
        if (isEnd()) {
            if (isVictory()) {
                victoryEndExecute.run();
            } else if (isDefeat()) {
                defeatEndExecute.run();
            } else {
                basicEndExecute.run();
            }
        }
    }

    protected abstract boolean isEnd();

    protected abstract boolean isVictory();

    protected abstract boolean isDefeat();

    public void moveCurrentPlayer(Location location) {
        //send message
        MovePlayerMessage movePlayerMessage = MovePlayerMessage.builder().build();
        socketClient.sendMatchData(
                match.getMatchId(),
                movePlayerMessage.getOpCode().ordinal(),
                movePlayerMessage.getPayload().getBytes(StandardCharsets.UTF_8));
    }

    public Optional<Player> getCurrentPlayer() {
        return getPlayer(session.getUserId());
    }

    public Optional<Player> getPlayer(String userId){
        return Optional.ofNullable(playerList
                .stream()
                .filter(player -> player.getUserId().equals(session.getUserId()))
                .collect(Collectors.toList()).get(0));
    }

    @Override
    public void onDisconnect(Throwable t) {
        super.onDisconnect(t);
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        String chatMessage = message.getUsername() + " : " + message.getContent();
        chattingLog.add(chatMessage);
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        long networkOpCode = matchData.getOpCode();
        byte[] networkBytes = matchData.getData();

        OpCode opCode = OpCode.values()[(int) networkOpCode];
        String data = new String(networkBytes,StandardCharsets.UTF_8);
        switch (opCode){
            case MOVE_PLAYER:
                MovePlayerMessage movePlayerMessage = gson.fromJson(data,MovePlayerMessage.class);
                Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
                optionalPlayer.ifPresent((player -> {
                    Location location = new Location("");
                    location.setLatitude(movePlayerMessage.getLatitude());
                    location.setLongitude(movePlayerMessage.getLongitude());
                    player.updateLocation(location);
                }));
                break;
            default:
                break;
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        List<Player> joinPlayerList = joinList.stream()
                .map((userPresence) -> new Player(userPresence.getUserId()))
                .collect(Collectors.toList());
        this.userPresenceList.addAll(joinList);
        this.playerList.addAll(joinPlayerList);

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        List<Player> leavePlayerList = leaveList.stream()
                .map((userPresence -> new Player(userPresence.getUserId())))
                .collect(Collectors.toList());
        this.userPresenceList.removeAll(leaveList);
        this.playerList.removeAll(leavePlayerList);

        // 새로 들어온 사람이 위치를 갱신할 수 있도록 이동메시지를 보낸다.
        getCurrentPlayer().ifPresent((player -> {
            player.getLocation().ifPresent(this::moveCurrentPlayer);
        }));
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        super.onNotifications(notifications);
    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {
        super.onStatusPresence(presence);
    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {
        super.onStreamPresence(presence);
    }

    @Override
    public void onStreamData(StreamData data) {
        super.onStreamData(data);
    }
}
