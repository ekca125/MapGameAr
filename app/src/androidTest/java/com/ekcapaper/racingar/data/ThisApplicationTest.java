package com.ekcapaper.racingar.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayClient;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ThisApplicationTest {
    public static Client client;
    public static Session session;

    public static Client client2;
    public static Session session2;

    public static ThisApplication thisApplication;

    @BeforeClass
    public static void init() throws Exception {
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);

        thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
        thisApplication.loginEmailSync(AccountStub.ID2,AccountStub.PASSWORD2);
    }
/*
    @Test
    public void getCurrentMatches() throws Exception {
        GameRoomPlayClient gameRoomPlayClient = new GameRoomPlayClient(client,session);
        try{
            assertTrue(gameRoomPlayClient.createMatch());
            assertNotNull(thisApplication.getCurrentMatches());
            assertTrue(1<=thisApplication.getCurrentMatches().getMatchesCount());
            assertNotNull(thisApplication.getCurrentRoomInfo());
            assertTrue(1<=thisApplication.getCurrentRoomInfo().size());
        } finally {
            gameRoomPlayClient.leaveMatch();
        }
    }

    @Test
    public void makeGameRoom() {
        boolean result = thisApplication.makeGameRoom(GameType.GAME_TYPE_FLAG, Duration.ofSeconds(600), MapRange.calculateMapRange(LocationStub.location,1));
        assertTrue(result);
    }
*/

    @Test
    public void createGroupSync() {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

        boolean result = thisApplication.createGroupSync(String.valueOf(threadLocalRandom.nextInt()),"test desc");
        assertTrue(result);
        result = thisApplication.leaveCurrentGroupSync();
        assertTrue(result);
    }

    @Test
    public void createMatchSync() {
        boolean result = thisApplication.createMatchSync(new AbstractSocketListener(){});
        assertTrue(result);
        result = thisApplication.leaveCurrentMatchSync();
        assertTrue(result);
    }
}