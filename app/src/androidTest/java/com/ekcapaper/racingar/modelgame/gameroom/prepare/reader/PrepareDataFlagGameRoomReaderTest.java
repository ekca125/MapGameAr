package com.ekcapaper.racingar.modelgame.gameroom.prepare.reader;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.reader.RoomInfoReader;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class PrepareDataFlagGameRoomReaderTest {
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
    public void readPrepareData() {
        PrepareDataFlagGameRoomReader roomInfoReader = new PrepareDataFlagGameRoomReader(client,session,matchId);
        PrepareDataFlagGameRoom prepareDataFlagGameRoom = roomInfoReader.readPrepareData();
        assertNotNull(prepareDataFlagGameRoom);
    }
}