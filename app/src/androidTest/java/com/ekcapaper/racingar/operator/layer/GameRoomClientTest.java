package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.UserPresence;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GameRoomClientTest {
    public static Client client;
    public static Session session;

    public static Client client2;
    public static Session session2;

    @BeforeClass
    public static void init() throws ExecutionException, InterruptedException {
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);

        client2 = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session2 = client.authenticateEmail(AccountStub.ID2, AccountStub.PASSWORD2).get();
        assertNotNull(session2);
    }


    @Test
    public void createMatch() {
        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        try{
            assertTrue(gameRoomClient.createMatch());
        } finally {
            gameRoomClient.leaveMatch();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void createMatchRepeat() {
        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        try{
            assertTrue(gameRoomClient.createMatch());
            assertTrue(gameRoomClient.createMatch());
        } finally {
            gameRoomClient.leaveMatch();
        }
    }

    @Test
    public void joinTest() {
        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        assertTrue(gameRoomClient.createMatch());
        GameRoomClient gameRoomClient2 = new GameRoomClient(client2,session2);
        assertTrue(gameRoomClient2.joinMatch(gameRoomClient.getMatchId()));
        gameRoomClient.leaveMatch();
        gameRoomClient2.leaveMatch();
    }

    @Test
    public void matchJoinLeave() {
        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        assertTrue(gameRoomClient.createMatch());
        GameRoomClient gameRoomClient2 = new GameRoomClient(client2,session2);
        assertTrue(gameRoomClient2.joinMatch(gameRoomClient.getMatchId()));
        gameRoomClient.leaveMatch();
        gameRoomClient2.leaveMatch();
    }
}