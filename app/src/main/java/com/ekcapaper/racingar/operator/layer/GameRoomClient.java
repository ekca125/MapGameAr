package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class GameRoomClient implements SocketListener {
    NakamaNetworkManager nakamaNetworkManager;
    @Getter
    private List<UserPresence> channelUserPresenceList;
    @Getter
    private List<UserPresence> matchUserPresenceList;
    @Getter
    private Match match;

    public GameRoomClient(NakamaNetworkManager nakamaNetworkManager){
        this.nakamaNetworkManager = nakamaNetworkManager;
        this.matchUserPresenceList = new ArrayList<>();
        this.channelUserPresenceList = new ArrayList<>();
        this.match = null;
    }

    public boolean createMatch(){
        match = nakamaNetworkManager.createMatchSync(this);
        return match != null;
    }

    public boolean joinMatch(String matchId){
        match = nakamaNetworkManager.joinMatchSync(this, matchId);
        onMatchJoinPresence(match.getPresences());
        return match != null;
    }

    @Override
    public void onDisconnect(Throwable t) {

    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {

    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        // join 처리
        List<UserPresence> joinList = presence.getJoins();
        if (joinList != null) {
            onChannelJoinPresence(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = presence.getLeaves();
        if (leaveList != null) {
            onChannelLeavePresence(leaveList);
        }
    }

    public void onChannelJoinPresence(List<UserPresence> joinList) {
        channelUserPresenceList.addAll(joinList);
    }

    public void onChannelLeavePresence(List<UserPresence> leaveList) {
        channelUserPresenceList.removeAll(leaveList);
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
            onMatchJoinPresence(joinList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            onMatchLeavePresence(leaveList);
        }
    }

    public void onMatchJoinPresence(List<UserPresence> joinList) {
        matchUserPresenceList.addAll(joinList);
    }

    public void onMatchLeavePresence(List<UserPresence> leaveList) {
        matchUserPresenceList.removeAll(leaveList);
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
