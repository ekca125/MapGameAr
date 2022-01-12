package com.ekcapaper.racingar.operator.maker;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class FlagGameRoomOperatorJoinMakerTest {
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

        matchId = "57db5c52-7e4e-4509-b4b2-b7c1a089deb7";
    }
    @Test
    public void make() {
        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client,session,matchId);
        FlagGameRoomOperator flagGameRoomOperator = (FlagGameRoomOperator) flagGameRoomOperatorJoinMaker.make();
        assertNotNull(flagGameRoomOperator);
    }
}