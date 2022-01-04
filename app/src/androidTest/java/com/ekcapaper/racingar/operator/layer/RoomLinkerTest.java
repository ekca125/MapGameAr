package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.GameStartMessage;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.ChannelType;
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

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class RoomLinkerTest {
    BaseRoomLinker roomLinker;

    @Before
    public void makeRoomLinker() throws ExecutionException, InterruptedException {
        Client client;
        Session session;
        SocketClient socketClient;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        assertNotNull(client);

        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);

        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
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
        });

        Match match = socketClient.createMatch().get();
        Channel channel = socketClient.joinChat(match.getMatchId(), ChannelType.ROOM).get();
        assertNotNull(match);
        assertNotNull(channel);

        this.roomLinker = BaseRoomLinker.builder()
                .client(client)
                .session(session)
                .socketClient(socketClient)
                .match(match)
                .chatChannel(channel)
                .build();
    }

    @Test
    public void sendDataTest(){
        roomLinker.sendMatchData(new GameStartMessage());
        roomLinker.getSocketClient().writeChatMessage(roomLinker.getChatChannel().getId(),"test");
    }
}