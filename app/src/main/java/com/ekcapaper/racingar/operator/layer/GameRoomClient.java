package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.network.GameMessage;
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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;

public class GameRoomClient implements SocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 메시지 로그
    @Getter
    private final List<String> chatLog;
    // 서버와의 연동을 의미하는 객체들(Realtime, Chat Channel)
    private Match match;
    private Channel channel;
    // 활동 상태
    private boolean activeGameRoom;

    public GameRoomClient(Client client, Session session) {
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        this.chatLog = new ArrayList<>();
        // null 초기화
        match = null;
        channel = null;
        activeGameRoom = false;
        // 콜백 연동
        this.socketClient.connect(session, this);
    }

    private String getChannelId(String matchId) {
        // match id 로부터 channel id를 얻어낸다.
        return "channel-" + RoomDataSpace.normalizeMatchId(matchId);
    }

    public String getChannelId(){
        return getChannelId(getMatchId());
    }

    public String getMatchId(){
        return RoomDataSpace.normalizeMatchId(match.getMatchId());
    }

    public List<UserPresence> getMatchUserPresenceList(){
        return match.getPresences();
    }

    public List<UserPresence> getChannelUserPresenceList(){
        return channel.getPresences();
    }

    public boolean createMatch() {
        if (activeGameRoom) {
            // 이미 활성화된 경우
            throw new IllegalStateException();
        }
        try {
            match = socketClient.createMatch().get();
            channel = socketClient.joinChat(getChannelId(match.getMatchId()), ChannelType.ROOM).get();
            activeGameRoom = true;
            return true;
        } catch (ExecutionException | InterruptedException e) {
            match = null;
            channel = null;
            activeGameRoom = false;
            return false;
        }
    }

    public boolean joinMatch(String matchId) {
        if (activeGameRoom) {
            // 이미 활성화된 경우
            throw new IllegalStateException();
        }
        try {
            match = socketClient.joinMatch(matchId).get();
            channel = socketClient.joinChat(getChannelId(match.getMatchId()), ChannelType.ROOM).get();
            activeGameRoom = true;
            return true;
        } catch (ExecutionException | InterruptedException e) {
            match = null;
            channel = null;
            activeGameRoom = false;
            return false;
        }
    }

    public void leaveMatch() {
        if (!activeGameRoom) {
            // 종료 명령은 활성화되지 않은 경우에는 무시된다.
            return;
        }
        String matchId = match.getMatchId();
        String chatChannelId = channel.getId();

        socketClient.leaveMatch(matchId);
        socketClient.leaveChat(chatChannelId);
    }

    public final void sendMatchData(GameMessage gameMessage) {
        if (!activeGameRoom) {
            // 활성화되지 않은 상태에서 명령을 호출하는 경우
            throw new IllegalStateException();
        }
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

    /*
        Code	Purpose	Source	Description
        0	chat message	user	All messages sent by users.
        1	chat update	user	A user updating a message they previously sent.
        2	chat remove	user	A user removing a message they previously sent.
        3	joined group	server	An event message for when a user joined the group.
        4	added to group	server	An event message for when a user was added to the group.
        5	left group	server	An event message for when a user left a group.
        6	kicked from group	server	An event message for when an admin kicked a user from the group.
        7  	promoted in group	server	An event message for when a user is promoted as a group admin.
        8	banned in group	server	An event message for when a user got banned from a group.
        9	demoted in group	server	An event message for when a user got demoted in group.
    */

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

        }

        // leave 처리
        List<UserPresence> leaveList = presence.getLeaves();
        if (leaveList != null) {

        }
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

        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {

        }
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
