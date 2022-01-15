package com.ekcapaper.racingar.operator;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.GameMessageEnd;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.operator.layer.GameRoomClient;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GameRoomClientTest {
    public static GameRoomClient gameRoomClient;
    public static Client client;
    public static Session session;

    @BeforeClass
    public static void init() throws ExecutionException, InterruptedException {
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);

        gameRoomClient = new GameRoomClient(client,session);
    }

    @Test(expected = NullPointerException.class)
    public void getPlayerNone() {
        Optional<Player> playerOptional = gameRoomClient.getPlayer("None Player");
        playerOptional.orElseThrow(NullPointerException::new);
    }

    @Test
    public void getCurrentPlayer() {
        Optional<Player> playerOptional = gameRoomClient.getPlayer(session.getUserId());
        playerOptional.orElseThrow(NullPointerException::new);
    }

    @Test
    public void createMatch() {
        assertSame(gameRoomClient.getGameStatus(), GameStatus.GAME_NOT_READY);
        boolean success = gameRoomClient.createMatch();
        assertTrue(success);
        assertSame(gameRoomClient.getGameStatus(), GameStatus.GAME_READY);
    }
/*
    @Test
    public void sendGameStartEndMessage() {
        gameRoomClient.declareGameStart();
        gameRoomClient.declareGameEnd();
    }
*/
    @Test
    public void onGameStart() {
        gameRoomClient.onGameStart(new GameMessageStart());
        assertSame(gameRoomClient.getGameStatus(), GameStatus.GAME_STARTED);
    }

    @Test
    public void onGameEnd() {
        gameRoomClient.onGameEnd(new GameMessageEnd());
        assertSame(gameRoomClient.getGameStatus(), GameStatus.GAME_END);
    }
}