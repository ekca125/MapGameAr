package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.network.Message;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class RoomLinker extends AbstractSocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
    private final Match match;
    private final Channel chatChannel;
    // 유저 프로필 (Realtime)
    private final List<UserPresence> realTimeUserPresenceList;
    // 유저 프로필 (Chat Channel)
    private final List<UserPresence> chatUserPresenceList;
    // 메시지 로그
    private final List<String> chatLog;

    public RoomLinker(@NonNull Client client,
                      @NonNull Session session,
                      @NonNull SocketClient socketClient,
                      @NonNull Match match,
                      @NonNull Channel chatChannel) throws ExecutionException, InterruptedException {
        // 서버와의 연동에 필요한 객체
        this.client = client;
        this.session = session;
        this.socketClient = socketClient;
        // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
        this.match = match;
        this.chatChannel = chatChannel;
        // 프로필들
        this.realTimeUserPresenceList = new ArrayList<>();
        this.chatUserPresenceList = new ArrayList<>();
        // 채팅 로그
        this.chatLog = new ArrayList<>();
        // 소켓 연결 및 매치 설정
        this.socketClient.connect(session, this).get();
    }

    // send Message
    public final void sendMatchData(Message message) {
        socketClient.sendMatchData(
                match.getMatchId(),
                message.getOpCode().ordinal(),
                message.getPayload().getBytes(StandardCharsets.UTF_8)
        );
    }

    // receive
    @Override
    public void onDisconnect(Throwable t) {
        // 연결이 끊어졌을 때에는 매치, 채팅의 연결을 끊는다.
        super.onDisconnect(t);
        String matchId = match.getMatchId();
        String chatChannelId = chatChannel.getId();

        socketClient.leaveMatch(matchId);
        socketClient.leaveChat(chatChannelId);
    }

    /*
        Code	Purpose	Source	Description
    0	chat message	user	All messages sent by users.
    1	chat update	user	A user updating a message they previously sent.
    2	chat remove	user	A user removing a message they previously sent.
    3	joined group	server	An event message for when a user joined the group.
    4	added to group	server	An event message for when a user was added to the group.
    5	left group	server	An event message for when a user left a group.
    6	kicked from group	server	An event message for when an admin kicked a user from the group.
    7	promoted in group	server	An event message for when a user is promoted as a group admin.
    8	banned in group	server	An event message for when a user got banned from a group.
    9	demoted in group	server	An event message for when a user got demoted in group.
    */
    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        String chat = null;

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
        super.onChannelPresence(presence);
        // join 처리
        List<UserPresence> joinList = presence.getJoins();
        if (joinList != null) {
            chatUserPresenceList.addAll(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = presence.getLeaves();
        if (leaveList != null) {
            chatUserPresenceList.removeAll(leaveList);
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            realTimeUserPresenceList.addAll(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            realTimeUserPresenceList.removeAll(leaveList);
        }
    }
}
