package com.ekcapaper.racingar.operator.maker;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class FlagGameRoomOperatorNewMakerTest {
    public static Client client;
    public static Session session;
    public static String matchId;

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
    public void make() {
        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session, Duration.ofSeconds(60), LocationStub.mapRange);
        FlagGameRoomOperator flagGameRoomOperator = (FlagGameRoomOperator) flagGameRoomOperatorNewMaker.make();
        assertNotNull(flagGameRoomOperator);
    }
}