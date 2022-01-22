package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import android.location.Location;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Session;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameRoomPlayClientTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    public static NakamaGameManager nakamaGameManager1;
    public static NakamaGameManager nakamaGameManager2;

    public static GameRoomPlayClient gameRoomPlayClient1;
    public static GameRoomPlayClient gameRoomPlayClient2;

    public static Session session1;
    public static Session session2;

    @BeforeClass
    public static void init() throws Exception {
        nakamaNetworkManager1 = new NakamaNetworkManager();
        nakamaNetworkManager2 = new NakamaNetworkManager();

        nakamaNetworkManager1.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        nakamaNetworkManager2.loginEmailSync(AccountStub.ID2,AccountStub.PASSWORD2);

        nakamaGameManager1 = new NakamaGameManager(nakamaNetworkManager1);
        nakamaGameManager2 = new NakamaGameManager(nakamaNetworkManager2);

        gameRoomPlayClient1 = new GameRoomPlayClient(nakamaNetworkManager1,nakamaGameManager1);
        gameRoomPlayClient2 = new GameRoomPlayClient(nakamaNetworkManager2,nakamaGameManager2);

        Class<NakamaNetworkManager> nakamaNetworkManagerClass = NakamaNetworkManager.class;
        Field[] fields = nakamaNetworkManagerClass.getDeclaredFields();
        Field sessionField = Arrays.stream(fields).filter(field->field.getName().equals("session")).collect(Collectors.toList()).get(0);
        sessionField.setAccessible(true);

        session1 = (Session) sessionField.get(nakamaNetworkManager1);
        session2 = (Session) sessionField.get(nakamaNetworkManager2);

        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc,gameRoomPlayClient1);
        assertTrue(result);

        result = nakamaGameManager2.joinGameRoom(roomName,gameRoomPlayClient2);
        assertTrue(result);
    }


    @Test
    public void test1declareGameStart() {
        // status
        assertEquals(gameRoomPlayClient1.getGameStatus(), GameStatus.GAME_READY);
        assertEquals(gameRoomPlayClient2.getGameStatus(), GameStatus.GAME_READY);
        // start
        gameRoomPlayClient1.declareGameStart();
        // mock
        gameRoomPlayClient2.onGameStart(new GameMessageStart());
        // test
        gameRoomPlayClient1.getPlayerOptional(session1.getUserId()).orElseThrow(NullPointerException::new);
        gameRoomPlayClient1.getPlayerOptional(session2.getUserId()).orElseThrow(NullPointerException::new);
        gameRoomPlayClient1.getPlayerOptional(session2.getUserId()).orElseThrow(NullPointerException::new);
        gameRoomPlayClient2.getPlayerOptional(session2.getUserId()).orElseThrow(NullPointerException::new);
        // status
        assertEquals(gameRoomPlayClient1.getGameStatus(), GameStatus.GAME_RUNNING);
        assertEquals(gameRoomPlayClient2.getGameStatus(), GameStatus.GAME_RUNNING);
    }

    @Test
    public void test2declareCurrentPlayerMove() {
        Location location = new Location("");
        location.setLatitude(1.0);
        location.setLongitude(100.0);
        gameRoomPlayClient1.declareCurrentPlayerMove(location);
        gameRoomPlayClient1.getCurrentPlayer().getLocation().orElseThrow(NullPointerException::new);
        gameRoomPlayClient1.getCurrentPlayer().getLocation().ifPresent(location1 -> {
            assertEquals(location1.getLatitude(), location.getLatitude(), 1.0);
            assertEquals(location1.getLongitude(), location.getLongitude(), 1.0);
        });
    }

    @Test
    public void test3declareGameEnd() {
        // status
        assertEquals(gameRoomPlayClient1.getGameStatus(), GameStatus.GAME_RUNNING);
        assertEquals(gameRoomPlayClient2.getGameStatus(), GameStatus.GAME_RUNNING);
        // end
        gameRoomPlayClient1.declareGameEnd();
        gameRoomPlayClient2.onGameEnd(new GameMessageEnd());
        // status
        assertEquals(gameRoomPlayClient1.getGameStatus(), GameStatus.GAME_END);
        assertEquals(gameRoomPlayClient2.getGameStatus(), GameStatus.GAME_END);
    }
}