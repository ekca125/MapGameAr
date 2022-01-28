package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.stub.AccountStub;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameRoomClientFactoryTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    @BeforeClass
    public static void init() throws Exception {
        nakamaNetworkManager1 = new NakamaNetworkManager();
        nakamaNetworkManager2 = new NakamaNetworkManager();
    }

    @Before
    public void login(){
        assertFalse(nakamaNetworkManager1.isLogin());
        assertFalse(nakamaNetworkManager2.isLogin());
        nakamaNetworkManager1.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        nakamaNetworkManager2.loginEmailSync(AccountStub.ID2,AccountStub.PASSWORD2);
        assertTrue(nakamaNetworkManager1.isLogin());
        assertTrue(nakamaNetworkManager2.isLogin());
    }

    @After
    public void logout(){
        nakamaNetworkManager1.logout();
        nakamaNetworkManager2.logout();
        assertFalse(nakamaNetworkManager1.isLogin());
        assertFalse(nakamaNetworkManager2.isLogin());
    }

    @Test
    public void createGameRoomClientNewMatch() {
        GameRoomClient gameRoomClient = GameRoomClientFactory.createGameRoomClientNewMatch(
                GameRoomClient.class.getName(),
                nakamaNetworkManager1,
                "gameroomclient"
        );
        assertNotNull(gameRoomClient);
    }

    @Test
    public void createGameRoomClientJoinMatch() {
        GameRoomClient gameRoomClient1 = GameRoomClientFactory.createGameRoomClientNewMatch(
                GameRoomClient.class.getName(),
                nakamaNetworkManager1,
                "gameroomclient"
        );
        assertNotNull(gameRoomClient1);

        GameRoomClient gameRoomClient2 = GameRoomClientFactory.createGameRoomClientJoinMatch(
                GameRoomClient.class.getName(),
                nakamaNetworkManager2,
                gameRoomClient1.getMatch().getMatchId()
        );
        assertNotNull(gameRoomClient2);
    }

    @Test
    public void createFlagGameRoomClientNewMatch() {
        GameRoomClient gameRoomClient = GameRoomClientFactory.createGameRoomClientNewMatch(
                FlagGameRoomClient.class.getName(),
                nakamaNetworkManager1,
                "gameroomclient"
        );
        assertNotNull(gameRoomClient);
    }

    @Test
    public void createFlagGameRoomClientJoinMatch() {
        GameRoomClient gameRoomClient1 = GameRoomClientFactory.createGameRoomClientNewMatch(
                FlagGameRoomClient.class.getName(),
                nakamaNetworkManager1,
                "gameroomclient"
        );
        assertNotNull(gameRoomClient1);

        GameRoomClient gameRoomClient2 = GameRoomClientFactory.createGameRoomClientJoinMatch(
                FlagGameRoomClient.class.getName(),
                nakamaNetworkManager2,
                gameRoomClient1.getMatch().getMatchId()
        );
        assertNotNull(gameRoomClient2);
    }
}