package com.ekcapaper.racingar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class ServerEngineTest {
    @Test
    public void functionTest() throws ExecutionException, InterruptedException {
        Client client;
        Session session;
        SocketClient socketClient;
        Match match;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                KeyStorageNakama.getWebSocketSSL()
        );
        assertNotNull(socketClient);

        match = socketClient.createMatch().get();
        assertNotNull(match);
    }

}
