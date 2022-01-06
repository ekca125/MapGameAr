package com.ekcapaper.racingar.operator.maker.newroom;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.AccountStub;
import com.ekcapaper.racingar.LocationStub;
import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomClient;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FlagGameRoomOperatorNewMakerTest {

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
    }

    @Test
    public void initTest() throws ExecutionException, InterruptedException {
        Client client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        Session session = client.authenticateEmail(AccountStub.ID, AccountStub.PASSWORD).get();
        assertNotNull(session);
    }

    @Test
    public void makeGameFlagList() {
        Duration duration = Duration.ofSeconds(100);
        MapRange mapRange = MapRange.calculateMapRange(LocationStub.location,1);

        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session,duration,mapRange);
        List<GameFlag> gameFlagList = flagGameRoomOperatorNewMaker.requestGameFlagList(mapRange);
        assertNotNull(gameFlagList);
    }

    @Test
    public void writeTest(){
        Duration duration = Duration.ofSeconds(100);
        MapRange mapRange = MapRange.calculateMapRange(LocationStub.location,1);

        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session,duration,mapRange);
        List<GameFlag> gameFlagList = flagGameRoomOperatorNewMaker.requestGameFlagList(mapRange);
        assertNotNull(gameFlagList);

        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, duration, gameFlagList);
        boolean matchProcessSuccess = flagGameRoomOperator.createMatch();
        assertTrue(matchProcessSuccess);

        Match match = flagGameRoomOperator.getMatch().get();
        String matchId = match.getMatchId();
        boolean writeResult = flagGameRoomOperatorNewMaker.writePrepareData(matchId,gameFlagList);
        assertTrue(writeResult);
    }


    @Test
    public void makeFlagGameRoomOperator() {
        Duration duration = Duration.ofSeconds(100);
        MapRange mapRange = MapRange.calculateMapRange(LocationStub.location,1);

        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client,session,duration,mapRange);
        FlagGameRoomOperator flagGameRoomOperator = flagGameRoomOperatorNewMaker.makeFlagGameRoomOperator();
        assertNotNull(flagGameRoomOperator);
    }


}