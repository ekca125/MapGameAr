package com.ekcapaper.racingar.operator.layer;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.stub.AccountStub;
import com.heroiclabs.nakama.Session;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GameRoomPlayOperatorTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    public static NakamaGameManager nakamaGameManager1;
    public static NakamaGameManager nakamaGameManager2;

    public static GameRoomPlayOperator gameRoomPlayOperator1;
    public static GameRoomPlayOperator gameRoomPlayOperator2;

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

        gameRoomPlayOperator1 = new GameRoomPlayOperator(nakamaNetworkManager1,nakamaGameManager1, Duration.ofSeconds(3));
        gameRoomPlayOperator2 = new GameRoomPlayOperator(nakamaNetworkManager2,nakamaGameManager2, Duration.ofSeconds(3));

        Class<NakamaNetworkManager> nakamaNetworkManagerClass = NakamaNetworkManager.class;
        Field[] fields = nakamaNetworkManagerClass.getDeclaredFields();
        Field sessionField = Arrays.stream(fields).filter(field->field.getName().equals("session")).collect(Collectors.toList()).get(0);
        sessionField.setAccessible(true);

        session1 = (Session) sessionField.get(nakamaNetworkManager1);
        session2 = (Session) sessionField.get(nakamaNetworkManager2);

        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";

        boolean result;
        result = nakamaGameManager1.createGameRoom(roomName,roomDesc,gameRoomPlayOperator1);
        assertTrue(result);

        result = nakamaGameManager2.joinGameRoom(roomName,gameRoomPlayOperator2);
        assertTrue(result);

        gameRoomPlayOperator1.declareGameStart();
        gameRoomPlayOperator2.onGameStart(new GameMessageStart());
    }

    @Test
    public void endCheck() throws InterruptedException {
        gameRoomPlayOperator1.declareGameStart();
        gameRoomPlayOperator2.onGameStart(new GameMessageStart());

        Thread.sleep(4000);
        assertSame(gameRoomPlayOperator1.getGameStatus(), GameStatus.GAME_END);
        assertSame(gameRoomPlayOperator1.getGameStatus(), GameStatus.GAME_END);
    }

}