package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.Message;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.ChannelType;
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
import java.util.function.Consumer;

import lombok.Getter;

public class RoomLinker extends AbstractSocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
    @Getter
    private Match currentMatch;
    @Getter
    private Channel currentChatChannel;
    // 유저 프로필 (Realtime)
    private final List<UserPresence> realTimeUserPresenceList;
    // 유저 프로필 (Chat Channel)
    private final List<UserPresence> chatUserPresenceList;

    public RoomLinker(Client client, Session session) throws ExecutionException, InterruptedException {
        // 서버와의 연동에 필요한 객체
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
        this.currentMatch = null;
        this.currentChatChannel = null;
        // 유저들의 프로필
        this.realTimeUserPresenceList = new ArrayList<>();
        // 유저들의 채팅 프로필과 채팅 데이터
        this.chatUserPresenceList = new ArrayList<>();
        // 소켓 연결 및 매치 설정
        this.socketClient.connect(session, this).get();
    }

    public final boolean createMatch(){
        try {
            this.currentMatch = socketClient.createMatch().get();
            this.currentChatChannel = socketClient.joinChat(this.currentMatch.getMatchId(),ChannelType.ROOM).get();
            return true;
        }
        catch (ExecutionException | InterruptedException e) {
            this.currentMatch = null;
            this.currentChatChannel = null;
            return false;
        }
    }

    public final boolean joinMatch(String matchId) {
        try {
            this.currentMatch = socketClient.joinMatch(matchId).get();
            this.currentChatChannel = socketClient.joinChat(this.currentMatch.getMatchId(),ChannelType.ROOM).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            this.currentMatch = null;
            this.currentChatChannel = null;
            return false;
        }
    }

    // send Message
    public final void sendMatchData(Message message) {
        socketClient.sendMatchData(
                currentMatch.getMatchId(),
                message.getOpCode().ordinal(),
                message.getPayload().getBytes(StandardCharsets.UTF_8)
        );
    }

    // receive
    @Override
    public void onDisconnect(Throwable t) {
        // 연결이 끊어졌을 때에는 즉시 종료
        super.onDisconnect(t);
        socketClient.leaveMatch(currentMatch.getMatchId());
        socketClient.leaveChat(currentChatChannel.getId());

        currentMatch = null;
        currentChatChannel = null;

        this.socketClient.disconnect();
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
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
        int messageCode = message.getCode().getValue();


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
