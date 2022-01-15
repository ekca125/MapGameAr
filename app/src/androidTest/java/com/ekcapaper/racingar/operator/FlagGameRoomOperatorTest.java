package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FlagGameRoomOperatorTest {
    public static FlagGameRoomPlayOperator flagGameRoomOperator;
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

        List<GameFlag> gameFlagList = new ArrayList<>();
        gameFlagList.add(new GameFlag(LocationStub.location));

        flagGameRoomOperator = new FlagGameRoomPlayOperator(client,session, Duration.ofSeconds(360),gameFlagList);
    }

    @Test
    public void onMovePlayer() {
        boolean result = flagGameRoomOperator.createMatch();
        assertTrue(result);

        flagGameRoomOperator.declareGameStart();

        int size1 = flagGameRoomOperator.getUnownedFlagList().size();
        assertEquals(1, size1);

        flagGameRoomOperator.onMovePlayer(new GameMessageMovePlayer(
                session.getUserId(),
                LocationStub.location.getLatitude(),
                LocationStub.location.getLongitude()
        ));

        int point = flagGameRoomOperator.getPoint(session.getUserId());
        assertEquals(1, point);

        int size2 = flagGameRoomOperator.getUnownedFlagList().size();
        assertEquals(0, size2);
    }

}