package com.ekcapaper.racingar.operator.impl;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorNewMaker;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FlagGameRoomPlayOperatorTest {
    public static Client client;
    public static Session session;
    public static String matchId;
    public static FlagGameRoomPlayOperator flagGameRoomOperator;
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
    public void onMovePlayer() {
        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session, Duration.ofSeconds(600), LocationStub.mapRange);
        flagGameRoomOperator = (FlagGameRoomPlayOperator) flagGameRoomOperatorNewMaker.make();
        assertNotNull(flagGameRoomOperator);

        // reflection test
        List<GameFlag> gameFlagList = flagGameRoomOperator.getUnownedFlagList();
        gameFlagList.stream().forEach(gameFlag -> {
            flagGameRoomOperator.declareCurrentPlayerMove(gameFlag.getLocation());
        });

        assertEquals(0,flagGameRoomOperator.getUnownedFlagList().size());
        //Log.d("testtest", String.valueOf(gameFlagList.size()));
    }

    @Test
    public void getPoint() {
        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session, Duration.ofSeconds(600), LocationStub.mapRange);
        flagGameRoomOperator = (FlagGameRoomPlayOperator) flagGameRoomOperatorNewMaker.make();
        assertNotNull(flagGameRoomOperator);

        // reflection test
        List<GameFlag> gameFlagList = flagGameRoomOperator.getUnownedFlagList();
        gameFlagList.stream().forEach(gameFlag -> {
            flagGameRoomOperator.declareCurrentPlayerMove(gameFlag.getLocation());
        });

        assertEquals(10,flagGameRoomOperator.getPoint(session.getUserId()));
    }

    @Test
    public void getUnownedFlagList() {
        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session, Duration.ofSeconds(600), LocationStub.mapRange);
        flagGameRoomOperator = (FlagGameRoomPlayOperator) flagGameRoomOperatorNewMaker.make();
        assertNotNull(flagGameRoomOperator);
        assertNotNull(flagGameRoomOperator.getUnownedFlagList());
    }
}