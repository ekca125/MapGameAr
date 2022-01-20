package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import com.heroiclabs.nakama.SocketListener;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.api.Group;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NakamaNetworkManagerTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;
    public static SocketListener socketListenerEmpty;

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
    public void createGroup() throws ExecutionException, InterruptedException {
        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";
        Group group = nakamaNetworkManager1.createGroupSync(groupName,groupDesc);
        assertNotNull(group);
        nakamaNetworkManager1.leaveGroupSync(group.getId());
    }

    public void joinGroup

}