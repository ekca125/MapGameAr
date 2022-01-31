package com.ekcapaper.mapgamear;

import static org.junit.Assert.assertNotNull;

import com.ekcapaper.mapgamear.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
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
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class ServerEngineTest {
    @Test
    public void functionTest() throws ExecutionException, InterruptedException {
        Client client;
        Session session;
        SocketClient socketClient;
        
        // client
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        // 소켓
        socketClient = client.createSocket();
        assertNotNull(socketClient);
        socketClient.connect(session, new AbstractSocketListener() {
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
        }).get();

        // 매치
        Match match = socketClient.createMatch().get();
        assertNotNull(match);
    }
}
