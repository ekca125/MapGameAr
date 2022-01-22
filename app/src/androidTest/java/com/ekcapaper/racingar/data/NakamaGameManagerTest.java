package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.ListenerStub;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class NakamaGameManagerTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    public static NakamaGameManager nakamaGameManager1;
    public static NakamaGameManager nakamaGameManager2;

    @BeforeClass
    public static void init() throws Exception {
        nakamaNetworkManager1 = new NakamaNetworkManager();
        nakamaNetworkManager2 = new NakamaNetworkManager();

        nakamaNetworkManager1.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        nakamaNetworkManager2.loginEmailSync(AccountStub.ID2,AccountStub.PASSWORD2);

        nakamaGameManager1 = new NakamaGameManager(nakamaNetworkManager1);
        nakamaGameManager2 = new NakamaGameManager(nakamaNetworkManager2);
    }

    @Test
    public void createGameRoom() {
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc, ListenerStub.socketListenerEmpty);
        assertTrue(result);

        nakamaGameManager1.leaveGameRoom();
    }

    @Test
    public void joinGameRoom(){
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc, ListenerStub.socketListenerEmpty);
        assertTrue(result);

        result = nakamaGameManager2.joinGameRoom(roomName,ListenerStub.socketListenerEmpty);
        assertTrue(result);

        nakamaGameManager1.leaveGameRoom();
    }

}