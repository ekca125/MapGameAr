package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import lombok.Getter;

public abstract class RoomLinker extends AbstractSocketListener {
    // 서버와의 연동에 필요한 객체
    private final Client client;
    private final Session session;
    private final SocketClient socketClient;
    // 방에 해당하는 객체
    @Getter
    private Optional<Match> currentMatch;
    // 정보
    private final List<UserPresence> userPresenceList;
    private final List<String> chattingLog;

    public RoomLinker(Client client, Session session) throws ExecutionException, InterruptedException {
        this.client = client;
        this.session = session;
        this.socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        this.socketClient.connect(session,this).get();
        this.currentMatch = Optional.empty();
        // 채팅 데이터
        this.chattingLog = new ArrayList<>();
        this.userPresenceList = new ArrayList<>();
    }

    public final void createMatch(){
        try {
            currentMatch = Optional.ofNullable(this.socketClient.createMatch().get());
        } catch (ExecutionException | InterruptedException e) {
            currentMatch = Optional.empty();
        }
    }

    public final void joinMatch(String matchId){
        try {
            currentMatch = Optional.ofNullable(this.socketClient.joinMatch(matchId).get());
        } catch (ExecutionException | InterruptedException e) {
            currentMatch = Optional.empty();
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
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
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
