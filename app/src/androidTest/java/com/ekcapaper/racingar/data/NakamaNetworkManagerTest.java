package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import android.util.Log;

import com.ekcapaper.racingar.stub.ListenerStub;
import com.google.gson.JsonObject;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketListener;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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


    @Test
    public void rwPublicServerStorageSync() throws IllegalAccessException {
        // nakamaNetworkManager1 session
        Class<NakamaNetworkManager> nakamaNetworkManagerClass = NakamaNetworkManager.class;
        Field[] fields = nakamaNetworkManagerClass.getDeclaredFields();
        Field sessionField = Arrays.stream(fields).filter(field->field.getName().equals("session")).collect(Collectors.toList()).get(0);
        sessionField.setAccessible(true);
        Session nakamaNetworkManager1Session = (Session) sessionField.get(nakamaNetworkManager1);

        // info
        String collectionName = "collectionTest";
        String keyName = "keyTest";
        // data
        Map<String,Object> data = new HashMap<>();
        data.put("test",RandomStringUtils.randomAlphabetic(10));
        // write
        boolean result = nakamaNetworkManager1.writePublicServerStorageSync(collectionName,keyName,data);
        assertTrue(result);
        // read
        Map<String,Object> resultMap = nakamaNetworkManager2.readServerStorageSync(collectionName,keyName,nakamaNetworkManager1Session.getUserId());
        assertNotNull(resultMap);
        // 데이터 테스트
        for(String mapKey:resultMap.keySet()){
            assertTrue(data.containsKey(mapKey));
            assertTrue(resultMap.containsKey(mapKey));
        }
        for(String mapKey:resultMap.keySet()){
            assertEquals(data.get(mapKey), resultMap.get(mapKey));
        }
    }

    @Test
    public void rwPublicServerStorageSync2() throws IllegalAccessException {
        // nakamaNetworkManager1 session
        Class<NakamaNetworkManager> nakamaNetworkManagerClass = NakamaNetworkManager.class;
        Field[] fields = nakamaNetworkManagerClass.getDeclaredFields();
        Field sessionField = Arrays.stream(fields).filter(field->field.getName().equals("session")).collect(Collectors.toList()).get(0);
        sessionField.setAccessible(true);
        Session nakamaNetworkManager2Session = (Session) sessionField.get(nakamaNetworkManager2);

        // info
        String collectionName = "collectionTest";
        String keyName = "keyTest";
        // data
        Map<String,Object> data = new HashMap<>();
        data.put("test",RandomStringUtils.randomAlphabetic(10));
        // write
        boolean result = nakamaNetworkManager2.writePublicServerStorageSync(collectionName,keyName,data);
        assertTrue(result);
        // read
        Map<String,Object> resultMap = nakamaNetworkManager1.readServerStorageSync(collectionName,keyName,nakamaNetworkManager2Session.getUserId());
        assertNotNull(resultMap);
        // 데이터 테스트
        for(String mapKey:resultMap.keySet()){
            assertTrue(data.containsKey(mapKey));
            assertTrue(resultMap.containsKey(mapKey));
        }
        for(String mapKey:resultMap.keySet()){
            assertEquals(data.get(mapKey), resultMap.get(mapKey));
        }
    }

}