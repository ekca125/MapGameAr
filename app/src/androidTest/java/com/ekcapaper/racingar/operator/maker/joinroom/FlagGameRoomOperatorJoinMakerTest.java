package com.ekcapaper.racingar.operator.maker.joinroom;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.LocationStub;
import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.newroom.FlagGameRoomOperatorNewMaker;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
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
        Duration duration = Duration.ofSeconds(100);
        String matchId = "d04c07d3-0075-49ee-acd6-e61805e254dd.";

        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client,session,matchId,duration);
        /*
        boolean result = flagGameRoomOperatorJoinMaker();
        assertTrue(result);

         */
    }
}