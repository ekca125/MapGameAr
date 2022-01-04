package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.Test;

public class RoomLinkerTest {
    @Test
    public void makeRoomLink(){
        Client client;
        Session session;


        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);




        RoomClient roomClient = new RoomClient(client,session);
        roomClient.createMatch();
        roomClient.getCurrentMatch().orElseThrow(IllegalStateException::new);

    }


    @Test
    public void sendMatchData() {


    }

}