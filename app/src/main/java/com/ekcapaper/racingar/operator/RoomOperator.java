package com.ekcapaper.racingar.operator;

import android.location.Location;

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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class RoomOperator extends AbstractSocketListener {
    // 서버에서의 유저들과 현재 방에서의 플레이어를 의미한다.
    List<UserPresence> userPresenceList;
    List<Player> playerList;
    // 서버와의 연동
    Session session;
    SocketClient socketClient;
    Match match;
    // 채팅 데이터
    List<String> chattingLog;

    public RoomOperator(Session session, SocketClient socketClient, Match match) {
        this.userPresenceList = new ArrayList<>();
        this.playerList = new ArrayList<>();

        this.session = session;
        this.socketClient = socketClient;
        this.match = match;

        socketClient.connect(session, this);
    }
    
    // 이런 꼴로 만든다.
    Consumer victory
    abstract protected void victoryCheck(){
        victory.accept();
    }

/*
    승리조건등도 콜백에서 이 클래스의 함수를 호출하는 것으로 한다.
    이걸로 콜백에서 처리하는 코드가 된다. 메시지는 단순히 보내는 것만 하도록 한다. final로
    public void moveCurrentPlayer(Location location) {
        Optional<Player> currentPlayer = Optional.ofNullable(playerList
                .stream()
                .filter(player -> player.getUserId().equals(session.getUserId()))
                .collect(Collectors.toList()).get(0));
        currentPlayer.ifPresent(player->player.updateLocation(location));
    }
*/
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
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // 등록 처리
        List<UserPresence> joins = matchPresence.getJoins();
        for (UserPresence userPresence : joins) {
            userPresenceList.add(userPresence);
            playerList.add(new Player(userPresence.getUserId()));
        }
        // 퇴장 처리
        for (UserPresence leave : matchPresence.getLeaves()) {
            for (int i = 0; i < userPresenceList.size(); i++) {
                if (userPresenceList.get(i).getUserId().equals(leave.getUserId())) {
                    userPresenceList.remove(i);
                }
            }
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getUserId().equals(leave.getUserId())) {
                    playerList.remove(i);
                }
            }
        }
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
