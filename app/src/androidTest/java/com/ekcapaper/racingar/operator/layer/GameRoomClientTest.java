package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.stub.AccountStub;

import org.junit.BeforeClass;

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

    }



}