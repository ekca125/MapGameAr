package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.stub.AccountStub;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameRoomClientTest {
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
    public void testJoinLeave(){
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc,gameRoomClient1);
        assertTrue(result);

        assertEquals(nakamaGameManager1.getGameRoomGroupUserList().getGroupUsersList().size(), 1);
        assertEquals(gameRoomClient1.matchUserPresenceList.size(),1);

        result = nakamaGameManager2.joinGameRoom(roomName,gameRoomClient2);
        assertTrue(result);

        assertEquals(nakamaGameManager1.getGameRoomGroupUserList().getGroupUsersList().size(), 2);
        assertEquals(nakamaGameManager2.getGameRoomGroupUserList().getGroupUsersList().size(), 2);
        nakamaGameManager2.leaveGameRoom();

        assertEquals(nakamaGameManager1.getGameRoomGroupUserList().getGroupUsersList().size(), 1);
        nakamaGameManager1.leaveGameRoom();
    }

    @Test
    public void testJoinMetadata(){
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc,gameRoomClient1);
        assertTrue(result);

        assertEquals(nakamaGameManager1.getGameRoomGroupUserList().getGroupUsersList().size(), 1);
        assertEquals(gameRoomClient1.matchUserPresenceList.size(),1);

        assertEquals(nakamaGameManager1.getGameRoomGroupUserList().getGroupUsersList().size(), 1);
        nakamaGameManager1.leaveGameRoom();
    }

}