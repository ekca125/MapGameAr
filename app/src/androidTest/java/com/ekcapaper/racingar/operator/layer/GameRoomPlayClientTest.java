package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class GameRoomPlayClientTest {

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
        GameRoomPlayClient gameRoomPlayClient = new GameRoomPlayClient(client,session);
        try{
            assertTrue(gameRoomPlayClient.createMatch());
        } finally {
            gameRoomPlayClient.leaveMatch();
        }
    }

    @Test
    public void joinMatch() {
        GameRoomPlayClient gameRoomPlayClient = new GameRoomPlayClient(client,session);
        GameRoomPlayClient gameRoomPlayClient2 = new GameRoomPlayClient(client2,session2);
        try{
            assertTrue(gameRoomPlayClient.createMatch());
            assertTrue(gameRoomPlayClient2.joinMatch(gameRoomClient.getMatchId()));
        } finally {
            gameRoomClient.leaveMatch();
            gameRoomClient2.leaveMatch();
        }
    }
}