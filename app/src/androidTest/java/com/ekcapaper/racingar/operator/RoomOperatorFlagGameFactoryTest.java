package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import android.location.Location;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.LocationStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class RoomOperatorFlagGameFactoryTest {
    private static Client client;
    private static Location location;
    private static Session session;
    private static SocketClient socketClient;
    private static Match match;
    private static double mapLengthKilometer;
    private static int timeLimitSecond;

    @BeforeClass
    public static void onlyOnce() throws ExecutionException, InterruptedException {
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                true
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        location = LocationStub.location;
        socketClient = client.createSocket(
                KeyStorageNakama.getWebSocketAddress(),
                KeyStorageNakama.getWebSocketPort(),
                true
        );
        match = socketClient.createMatch().get();
        mapLengthKilometer = 1.0;
        timeLimitSecond = 60;
    }


    @Test
    public void createRoomOperator() {
        RoomOperatorFlagGameFactory roomOperatorFlagGameFactory = RoomOperatorFlagGameFactory
                .builder()
                .location(location)
                .mapLengthKilometer(mapLengthKilometer)
                .match(match)
                .session(session)
                .socketClient(socketClient)
                .timeLimitSecond(timeLimitSecond)
                .build();
        RoomOperator roomOperator = roomOperatorFlagGameFactory.createRoomOperator();
        roomOperator.startGame();
        Assert.assertFalse(roomOperator.isEnd());
    }
}