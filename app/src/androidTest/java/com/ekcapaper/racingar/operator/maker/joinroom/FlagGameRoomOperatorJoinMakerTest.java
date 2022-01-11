package com.ekcapaper.racingar.operator.maker.joinroom;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class FlagGameRoomOperatorJoinMakerTest {
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
    public void readPrepareData() {
        String matchId = "0abdc362-04d9-4664-823a-4c9c4553d9de";

        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client,session,matchId);
        boolean result = flagGameRoomOperatorJoinMaker.readPrepareData();
        assertTrue(result);
    }

    @Test
    public void readRoomInfo() {
        String matchId = "0abdc362-04d9-4664-823a-4c9c4553d9de";

        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client,session,matchId);
        boolean result = flagGameRoomOperatorJoinMaker.readRoomInfo();
        assertTrue(result);
    }

    @Test
    public void makeFlagGameRoomOperator() {
        String matchId = "0abdc362-04d9-4664-823a-4c9c4553d9de";

        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorJoinMaker(client,session,matchId);
        FlagGameRoomOperator flagGameRoomOperator = flagGameRoomOperatorNewMaker.makeFlagGameRoomOperator();
        assertNotNull(flagGameRoomOperator);
    }
}