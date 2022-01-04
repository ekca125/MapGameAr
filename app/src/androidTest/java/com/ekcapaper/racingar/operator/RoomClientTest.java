package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class RoomClientTest {

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

        RoomClient roomClient = RoomClient.builder()
                .client(client)
                .session(session)
                .build();
        boolean success = roomClient.createMatch();
        assertTrue(success);
    }

    @Test
    public void joinMatch() {
    }
}