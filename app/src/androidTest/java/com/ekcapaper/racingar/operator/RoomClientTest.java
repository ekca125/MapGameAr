package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.activity.raar.stub.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.layer.RoomClient;
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

        RoomClient roomClient = new RoomClient(client,session);
        boolean success = roomClient.createMatch();
        assertTrue(success);
    }
}