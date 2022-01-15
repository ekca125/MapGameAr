package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.GameMessage;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;

public class GameRoomClient implements SocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 유저 프로필 (Realtime)
    private List<UserPresence> matchUserPresenceList;
    // 유저 프로필 (Chat Channel)
    private List<UserPresence> channelUserPresenceList;
    private final Channel channel;
    // 메시지 로그
    private final List<String> chatLog;
    // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
    @Getter
    private Match match;

    public GameRoomClient(Client client, Session session) {
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        this.matchUserPresenceList = new ArrayList<>();
        this.channelUserPresenceList = new ArrayList<>();
        this.chatLog = new ArrayList<>();
        // null 초기화
        match = null;
        channel = null;
        // 콜백 연동
        this.socketClient.connect(session, this);
    }

    public boolean createMatch() {
        try {
            match = socketClient.createMatch().get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            match = null;
            return false;
        }
    }

    public boolean joinMatch(String matchId) {
        try {
            match = socketClient.joinMatch(matchId).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            match = null;
            return false;
        }
    }

    public void leaveMatch() {
        String matchId = match.getMatchId();
        String chatChannelId = channel.getId();

        socketClient.leaveMatch(matchId);
        socketClient.leaveChat(chatChannelId);
    }

    public final void sendMatchData(GameMessage gameMessage) {
        socketClient.sendMatchData(
                match.getMatchId(),
                gameMessage.getOpCode().ordinal(),
                gameMessage.getPayload().getBytes(StandardCharsets.UTF_8)
        );
    }


    @Override
    public void onDisconnect(Throwable t) {
        leaveMatch();
    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        String chat;
        int messageCode = message.getCode().getValue();
        switch (messageCode) {
            case 0:
                chat = message.getUsername() + " : " + message.getContent();
                chatLog.add(chat);
                break;
            case 1:
                chat = message.getUsername() + " 플레이어가 입장했습니다.";
                chatLog.add(chat);
                break;
            case 2:
                chat = message.getUsername() + " 플레이어가 퇴장했습니다.";
                chatLog.add(chat);
                break;
            default:
                break;
        }
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        // join 처리
        List<UserPresence> joinList = presence.getJoins();
        if (joinList != null) {
            channelUserPresenceList.addAll(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = presence.getLeaves();
        if (leaveList != null) {
            channelUserPresenceList.removeAll(leaveList);
        }
        channelUserPresenceList = channelUserPresenceList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {

    }

    @Override
    public void onMatchData(MatchData matchData) {

    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            matchUserPresenceList.addAll(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            matchUserPresenceList.removeAll(leaveList);
        }
        matchUserPresenceList = matchUserPresenceList.stream().distinct().collect(Collectors.toList());
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
