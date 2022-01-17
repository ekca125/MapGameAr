package com.ekcapaper.racingar.modelgame.gameroom.info.reader;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.api.StorageObjectList;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class RoomInfoReaderTest {
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
        session = client.authenticateEmail(AccountStub.ID2, AccountStub.PASSWORD2).get();
        assertNotNull(session);

        matchId = "00f13389-9a56-47d6-8760-34e3688dd4fa";
    }

    @Test
    public void readRoomInfo() {
        RoomInfoReader roomInfoReader = new RoomInfoReader(client,session,matchId);
        RoomInfo roomInfo = roomInfoReader.readRoomInfo();
        assertNotNull(roomInfo);
    }

    @Test
    public void readTest() throws ExecutionException, InterruptedException {
        int limit = 3;
        String cursor = null;

        StorageObjectList objects = client.listUsersStorageObjects(session, RoomDataSpace.getCollectionName(matchId), cursor,limit).get();
        objects.getObjectsList().forEach(object -> Log.d("Key: {}", object.getKey()));
        objects.getObjectsList().forEach(object -> Log.d("Key: {}", object.getUserId()));
    }
}