package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.LocationStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;

public class RoomClientTest {

    @Test
    public void createRoom() throws ExecutionException, InterruptedException {
        Client client;
        Session session;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        RoomClient roomClient = new RoomClient(client,session);
        roomClient.createMatch();
        roomClient.getCurrentMatch().orElseThrow(IllegalStateException::new);
    }

    @Test
    public void sendMessage() throws ExecutionException, InterruptedException {
        Client client;
        Session session;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        RoomClient roomClient = new RoomClient(client,session);
        roomClient.createMatch();
        roomClient.getCurrentMatch().orElseThrow(IllegalStateException::new);

        MovePlayerMessage movePlayerMessage = MovePlayerMessage.builder()
                .userId(session.getUserId())
                .latitude(LocationStub.latitude)
                .longitude(LocationStub.longitude)
                .build();
        roomClient.sendMatchData(movePlayerMessage);
    }

    @Test
    public void onMovePlayerMessage() throws ExecutionException, InterruptedException {
        Client client;
        Session session;

        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID,AccountStub.PASSWORD).get();
        assertNotNull(session);

        RoomClient roomClient = new RoomClient(client,session);
        roomClient.createMatch();
        roomClient.getCurrentMatch().orElseThrow(IllegalStateException::new);

        MovePlayerMessage movePlayerMessage = MovePlayerMessage.builder()
                .userId(session.getUserId())
                .latitude(LocationStub.latitude)
                .longitude(LocationStub.longitude)
                .build();
        roomClient.onMovePlayer(movePlayerMessage);

        roomClient.getPlayer(session.getUserId()).orElseThrow(()->new IllegalStateException("not found : " + session.getUserId()));
        roomClient.getPlayer(session.getUserId()).ifPresent(player -> {
            player.getLocation().orElseThrow(NullPointerException::new);
            player.getLocation().ifPresent(location -> {
                assertEquals(location.getLatitude(), LocationStub.latitude, 0.0);
                assertEquals(location.getLongitude(), LocationStub.longitude, 0.0);
            });
        });
    }
}