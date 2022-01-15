package com.ekcapaper.racingar.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayClient;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

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
        thisApplication.login(AccountStub.ID2,AccountStub.PASSWORD2);
        thisApplication.getSessionOptional().orElseThrow(() -> new Exception("Login Error"));
    }

    @Test
    public void getCurrentMatches() throws Exception {
        GameRoomPlayClient gameRoomPlayClient = new GameRoomPlayClient(client,session);
        try{
            assertTrue(gameRoomPlayClient.createMatch());
            assertNotNull(thisApplication.getCurrentMatches());
            assertEquals(1,thisApplication.getCurrentMatches().getMatchesCount());
            assertNotNull(thisApplication.getCurrentRoomInfo());
            assertEquals(1,thisApplication.getCurrentRoomInfo().size());
        } finally {
            gameRoomPlayClient.leaveMatch();
        }
    }

    @Test
    public void makeGameRoom() {
        boolean result = thisApplication.makeGameRoom(GameType.GAME_TYPE_FLAG, Duration.ofSeconds(600), MapRange.calculateMapRange(LocationStub.location,1));
        assertTrue(result);
    }
}