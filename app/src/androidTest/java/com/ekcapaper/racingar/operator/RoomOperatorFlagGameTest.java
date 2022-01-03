package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RoomOperatorFlagGameTest {

    @Test
    public void onGameStart() throws ExecutionException, InterruptedException {
        Client client;
        Session session;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        RoomOperatorFlagGame roomOperatorFlagGame = new RoomOperatorFlagGame(client,session, new ArrayList<>());
        roomOperatorFlagGame.createMatch();
        roomOperatorFlagGame.getCurrentMatch().orElseThrow(IllegalStateException::new);

    }

    @Test
    public void isEnd() throws ExecutionException, InterruptedException {
    }
}