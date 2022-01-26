package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.api.Group;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NakamaGameManagerTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    public static NakamaGameManager nakamaGameManager1;
    public static NakamaGameManager nakamaGameManager2;

    public static GameRoomClient gameRoomClient1;
    public static GameRoomClient gameRoomClient2;

    @BeforeClass
    public static void init() throws Exception {
        nakamaNetworkManager1 = new NakamaNetworkManager();
        nakamaNetworkManager2 = new NakamaNetworkManager();

        nakamaNetworkManager1.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        nakamaNetworkManager2.loginEmailSync(AccountStub.ID2,AccountStub.PASSWORD2);

        nakamaGameManager1 = new NakamaGameManager(nakamaNetworkManager1);
        nakamaGameManager2 = new NakamaGameManager(nakamaNetworkManager2);

        gameRoomClient1 = new GameRoomClient(nakamaNetworkManager1,nakamaGameManager1);
        gameRoomClient2 = new GameRoomClient(nakamaNetworkManager2,nakamaGameManager2);
    }

    @Test
    public void groupMetaTest(){
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = RandomStringUtils.randomAlphabetic(10);

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc,gameRoomClient1);
        assertTrue(result);

        Group group = nakamaGameManager1.getRoomGroup();
        NakamaRoomMetaDataManager nakamaRoomMetaDataManager = new NakamaRoomMetaDataManager(nakamaNetworkManager1);
        Map<String,Object> metadata = new HashMap<>();
        metadata.put("test",RandomStringUtils.randomAlphabetic(10));

        result = nakamaRoomMetaDataManager.writeRoomMetaDataSync(group,metadata);
        assertTrue(result);

        Map<String,Object> metaData2 = nakamaRoomMetaDataManager.readRoomMetaDataSync(group);
        assertNotNull(metaData2);

        Log.d("test",group.getDescription());
        metaData2.forEach((a,b)->Log.d("test",a.toString() +"  "+ b));
    }

}