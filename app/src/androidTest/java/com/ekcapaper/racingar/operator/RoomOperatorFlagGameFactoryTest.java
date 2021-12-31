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
    @Test
    public void createRoomOperator() throws ExecutionException, InterruptedException {
        Client client;
        Location location;
        Session session;
        SocketClient socketClient;
        Match match;
        double mapLengthKilometer;
        int timeLimitSecond;

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