package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.Player;
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

import java.util.ArrayList;
import java.util.List;

public class RoomOperator extends AbstractSocketListener{
    // 서버에서의 유저들과 현재 방에서의 플레이어를 의미한다.
    List<UserPresence> userPresenceList;
    List<Player> playerList;
    // 서버와의 연동
    Session session;
    SocketClient socketClient;
    Match match;

    public RoomOperator(Session session, SocketClient socketClient, Match match) {
        this.userPresenceList = new ArrayList<>();
        this.playerList = new ArrayList<>();

        this.session = session;
        this.socketClient = socketClient;
        this.match = match;

        socketClient.connect(session,this);
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
        userPresenceList.addAll(matchPresence.getJoins());
        for (UserPresence leave : matchPresence.getLeaves()) {
            for (int i = 0; i < userPresenceList.size(); i++) {
                if (userPresenceList.get(i).getUserId().equals(leave.getUserId())) {
                    userPresenceList.remove(i);
                }
            }
        };
        // 새로 들어온 사람이 위치를 갱신할 수 있도록 이동메시지를 보낸다.
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
