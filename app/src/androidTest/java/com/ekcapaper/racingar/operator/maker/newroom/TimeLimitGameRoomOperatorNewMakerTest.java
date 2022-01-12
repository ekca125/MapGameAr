package com.ekcapaper.racingar.operator.maker.newroom;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class TimeLimitGameRoomOperatorNewMakerTest {
    public static Client client;
    public static Session session;

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
    }

    @Test
    public void getPrepareData() {
        TimeLimitGameRoomOperatorNewMaker timeLimitGameRoomOperatorNewMaker = new TimeLimitGameRoomOperatorNewMaker(client,session, Duration.ofSeconds(100));
        timeLimitGameRoomOperatorNewMaker.makeTimeLimitGameRoomOperator();
    }

    @Test
    public void writePrepareData() {

    }

    @Test
    public void makeTimeLimitGameRoomOperator() {
    }
}