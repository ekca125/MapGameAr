package com.ekcapaper.mapgamear.stub;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

public class ListenerStub {
    public static SocketListener socketListenerEmpty;
    static{
        socketListenerEmpty = new SocketListener() {
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

            }

            @Override
            public void onMatchmakerMatched(MatchmakerMatched matched) {

            }

            @Override
            public void onMatchData(MatchData matchData) {

            }

            @Override
            public void onMatchPresence(MatchPresenceEvent matchPresence) {

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
        };
    }
}
