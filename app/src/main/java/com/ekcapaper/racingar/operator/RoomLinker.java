package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.Message;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
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

import lombok.Getter;

public class RoomLinker extends AbstractSocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 유저의 정보
    private final List<UserPresence> realTimeUserPresenceList;
    // 채팅 데이터
    private final List<UserPresence> chatUserPresenceList;
    private final List<String> chatLog;
    // 매치 ID와 채팅 ID
    private String matchId;
    private String chatChannelId;
    // 서버와 연동된 매치와 채팅
    @Getter
    private Optional<Match> currentMatch;
    private Optional<Channel> currentChannel;

    public RoomLinker(Client client, Session session) throws ExecutionException, InterruptedException {
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        // 유저들의 프로필
        this.realTimeUserPresenceList = new ArrayList<>();
        // 유저들의 채팅 프로필과 채팅 데이터
        this.chatUserPresenceList = new ArrayList<>();
        this.chatLog = new ArrayList<>();
        // 소켓 연결 및 매치 설정
        this.socketClient.connect(session, this).get();
        this.currentMatch = Optional.empty();
    }

    // 방 생성 또는 입장
    public final void createMatch() {
        try {
            this.currentMatch = Optional.ofNullable(this.socketClient.createMatch().get());


        } catch (ExecutionException | InterruptedException e) {
            currentMatch = Optional.empty();
        }
    }

    public final void joinMatch(String matchId) {
        try {
            currentMatch = Optional.ofNullable(this.socketClient.joinMatch(matchId).get());
        } catch (ExecutionException | InterruptedException e) {
            currentMatch = Optional.empty();
        }
    }

    // send Message
    public final void sendMatchData(Message message) {
        currentMatch.ifPresent(match -> {
            socketClient.sendMatchData(
                    match.getMatchId(),
                    message.getOpCode().ordinal(),
                    message.getPayload().getBytes(StandardCharsets.UTF_8)
            );
        });
    }

    // receive
    @Override
    public void onDisconnect(Throwable t) {
        // 연결이 끊어졌을 때
        super.onDisconnect(t);
        // 현재 매치 무효화
        this.currentMatch.ifPresent((match)->{
            String matchId = match.getMatchId();
            this.socketClient.leaveMatch(matchId);
            this.socketClient.leaveChat();
        });

        this.socketClient.leaveMatch();

        this.currentMatch = Optional.empty();

    }

    @Override
    public void onError(Error error) {
        super.onError(error);
    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        chatLog.add(message.getUsername() + " : " + message.getContent());
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
        // join 처리
        Optional<List<UserPresence>> joinListOptional = Optional.ofNullable(presence.getJoins());
        joinListOptional.ifPresent(this.chatUserPresenceList::addAll);

        // leave 처리
        Optional<List<UserPresence>> leaveListOptional = Optional.ofNullable(presence.getLeaves());
        leaveListOptional.ifPresent(this.chatUserPresenceList::removeAll);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        Optional<List<UserPresence>> joinListOptional = Optional.ofNullable(matchPresence.getJoins());
        joinListOptional.ifPresent(this.realTimeUserPresenceList::addAll);

        // leave 처리
        Optional<List<UserPresence>> leaveListOptional = Optional.ofNullable(matchPresence.getLeaves());
        leaveListOptional.ifPresent(this.realTimeUserPresenceList::removeAll);
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
