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

        matchId = "99458b9f-0c09-4d5e-8d6a-4272c197ac3f";
    }

}