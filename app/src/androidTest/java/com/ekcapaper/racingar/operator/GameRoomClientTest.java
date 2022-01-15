package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.layer.GameRoomClient;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class GameRoomClientTest {

    @Test
    public void createMatch() throws ExecutionException, InterruptedException {
        Client client;
        Session session;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);

        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        boolean success = gameRoomClient.createMatch();
        assertTrue(success);
    }
}