package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.stub.ListenerStub;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.SocketListener;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NakamaNetworkManagerTest {
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
    public void createGroup(){
        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";

        Group group1 = nakamaNetworkManager1.createGroupSync(groupName,groupDesc);
        assertNotNull(group1);
        nakamaNetworkManager1.leaveGroupSync(group1.getName());
    }

    @Test
    public void joinGroup() throws ExecutionException, InterruptedException{
        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";

        Group group1 = nakamaNetworkManager1.createGroupSync(groupName,groupDesc);
        assertNotNull(group1);
        Group group2 = nakamaNetworkManager2.joinGroupSync(groupName);
        assertNotNull(group2);
        assertEquals(group1.getId(), group2.getId());

        GroupUserList groupUserList1 = nakamaNetworkManager1.getGroupUserList(groupName);
        GroupUserList groupUserList2 = nakamaNetworkManager2.getGroupUserList(groupName);

        assertEquals(groupUserList1.getGroupUsersCount(), groupUserList2.getGroupUsersCount());
        assertEquals(groupUserList1.getGroupUsersCount(), 2);
        assertEquals(groupUserList2.getGroupUsersCount(), 2);

        nakamaNetworkManager2.leaveGroupSync(groupName);
        groupUserList1 = nakamaNetworkManager1.getGroupUserList(groupName);
        assertEquals(groupUserList1.getGroupUsersCount(), 1);
        nakamaNetworkManager1.leaveGroupSync(groupName);
     }

    @Test
    public void createMatch(){
        Match match1 = nakamaNetworkManager1.createMatchSync(ListenerStub.socketListenerEmpty);
        assertNotNull(match1);
        nakamaNetworkManager1.leaveMatchSync(match1.getMatchId());
    }

    @Test
    public void joinMatch(){
        Match match1 = nakamaNetworkManager1.createMatchSync(ListenerStub.socketListenerEmpty);
        assertNotNull(match1);
        Match match2 = nakamaNetworkManager2.joinMatchSync(ListenerStub.socketListenerEmpty,match1.getMatchId());
        assertNotNull(match2);

        assertEquals(match1.getMatchId(), match2.getMatchId());

        nakamaNetworkManager1.leaveMatchSync(match1.getMatchId());
        nakamaNetworkManager2.leaveMatchSync(match2.getMatchId());
    }

}