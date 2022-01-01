package com.ekcapaper.racingar.operator;

import android.graphics.Path;
import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
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



    // 유틸리티
    private final Gson gson = new Gson();
    // 상태
    private boolean started;

    public RoomOperator(Client client ,Session session) {
        // 정보
        this.userPresenceList = new ArrayList<>();
        this.playerList = new ArrayList<>();
        this.chattingLog = new ArrayList<>();
        // 서버
        this.client = client;
        this.session = session;
        // 종료조건 콜백
        this.victoryEndExecute = () -> {
        };
        this.defeatEndExecute = () -> {
        };
        this.basicEndExecute = () -> {
        };
        // 서버와 연동
        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        socketClient.connect(session, this);

        //
        started = false;
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
            endSequence();
        }
    }

    private void endSequence(){
        socketClient.leaveMatch(match.getMatchId());
    }

    public void startGame(){
        started = true;
        // 종료조건 확인 시작
        scheduledExecutorServiceEndCheck = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorServiceEndCheck.scheduleWithFixedDelay(this::endCheck,1,1, TimeUnit.SECONDS);
    }

    protected boolean isStarted(){
        return started;
    }

    protected boolean isEnd(){
        return false;
    }

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
        try {
            return Optional.ofNullable(playerList
                    .stream()
                    .filter(player -> player.getUserId().equals(session.getUserId()))
                    .collect(Collectors.toList()).get(0));
        }
        catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
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
                onMovePlayer(movePlayerMessage);
                break;
            default:
                break;
        }
    }

    protected void onMovePlayer(MovePlayerMessage movePlayerMessage){
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(movePlayerMessage.getLatitude());
            location.setLongitude(movePlayerMessage.getLongitude());
            player.updateLocation(location);
        }));
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
