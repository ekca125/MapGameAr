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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import lombok.Setter;

public abstract class BaseRoomOperator extends AbstractSocketListener {
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 방
    private Optional<Match> matchOptional;
    // 서버에서의 유저들과 현재 방에서의 플레이어를 의미한다.
    private final List<UserPresence> userPresenceList;
    // 채팅 데이터
    private final List<String> chattingLog;

    public BaseRoomOperator(Client client, Session session) throws ExecutionException, InterruptedException {
        // 서버와의 연동
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        this.socketClient.connect(session,this).get();
        this.matchOptional = Optional.empty();
        // 채팅 데이터
        this.chattingLog = new ArrayList<>();
        this.userPresenceList = new ArrayList<>();
    }

    public final void createMatch(){
        try {
            matchOptional = Optional.ofNullable(this.socketClient.createMatch().get());
        } catch (ExecutionException | InterruptedException e) {
            matchOptional = Optional.empty();
        }
    }

    public final void joinMatch(String matchId){
        try {
            matchOptional = Optional.ofNullable(this.socketClient.joinMatch(matchId).get());
        } catch (ExecutionException | InterruptedException e) {
            matchOptional = Optional.empty();
        }
    }

    public Optional<Match> getMatchOptional() {
        return matchOptional;
    }

    // message function
    public final void moveCurrentPlayer(Location location) {
        //send message
        matchOptional.ifPresent(match -> {
            MovePlayerMessage movePlayerMessage = MovePlayerMessage.builder().build();
            socketClient.sendMatchData(
                    match.getMatchId(),
                    movePlayerMessage.getOpCode().ordinal(),
                    movePlayerMessage.getPayload().getBytes(StandardCharsets.UTF_8));
        });
    }

    public void startSequence(){
        matchOptional.orElseThrow(IllegalStateException::new);
    }
    public abstract void victorySequence();
    public abstract void defeatSequence();

    // 나가기
    public void finishSequence(){
        matchOptional.ifPresent(match -> {
            socketClient.leaveMatch(match.getMatchId());
            socketClient.disconnect();
        });
    }

    public String getCurrentUserId(){
        return session.getUserId();
    }

    // 콜백
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
    public final void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
    }

    @Override
    public final void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        Gson gson = new Gson();

        long networkOpCode = matchData.getOpCode();
        byte[] networkBytes = matchData.getData();

        OpCode opCode = OpCode.values()[(int) networkOpCode];
        String data = new String(networkBytes, StandardCharsets.UTF_8);
        switch (opCode){
            case MOVE_PLAYER:
                MovePlayerMessage movePlayerMessage = gson.fromJson(data,MovePlayerMessage.class);
                onMovePlayer(movePlayerMessage);
                break;
            default:
                break;
        }
    }

    protected abstract void onMovePlayer(MovePlayerMessage movePlayerMessage);

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        this.userPresenceList.addAll(joinList);

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        this.userPresenceList.removeAll(leaveList);
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
