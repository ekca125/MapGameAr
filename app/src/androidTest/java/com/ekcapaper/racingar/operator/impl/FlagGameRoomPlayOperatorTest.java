package com.ekcapaper.racingar.operator.impl;

import static org.junit.Assert.*;

import android.location.Location;

import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.Session;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import retrofit2.Call;
import retrofit2.Response;

public class FlagGameRoomPlayOperatorTest {
    public static NakamaNetworkManager nakamaNetworkManager1;
    public static NakamaNetworkManager nakamaNetworkManager2;

    public static NakamaGameManager nakamaGameManager1;
    public static NakamaGameManager nakamaGameManager2;

    public static FlagGameRoomPlayOperator gameRoomPlayOperator1;
    public static FlagGameRoomPlayOperator gameRoomPlayOperator2;

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

        double startLatitude = LocationStub.startLatitude;
        double startLongitude = LocationStub.startLongitude;
        double endLatitude = LocationStub.endLatitude;
        double endLongitude = LocationStub.endLongitude;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
        Response<List<AddressDto>> response = requester.execute();
        assertTrue(response.isSuccessful());

        List<GameFlag> mapList = response.body().stream().map(addressDto -> {
            Location location = new Location("");
            location.setLatitude(addressDto.getLatitude());
            location.setLongitude(addressDto.getLongitude());
            return new GameFlag(location);
        }).collect(Collectors.toList());


        gameRoomPlayOperator1 = new FlagGameRoomPlayOperator(nakamaNetworkManager1,nakamaGameManager1, Duration.ofSeconds(3), mapList);
        gameRoomPlayOperator2 = new FlagGameRoomPlayOperator(nakamaNetworkManager2,nakamaGameManager2, Duration.ofSeconds(3), mapList);

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
    public void testFlagGame() {
        List<GameFlag> gameFlagList = gameRoomPlayOperator1.getUnownedFlagList();

        int maxPoint = gameFlagList.size();
        int currentPoint;
        currentPoint = gameRoomPlayOperator1.getPoint(session1.getUserId());

        assertTrue(maxPoint>0);
        assertEquals(0, currentPoint);

        gameFlagList.stream().forEach(gameFlag -> {
            gameRoomPlayOperator1.declareCurrentPlayerMove(gameFlag.getLocation());
        });

        currentPoint = gameRoomPlayOperator1.getPoint(session1.getUserId());
        assertEquals(maxPoint,currentPoint);
    }

}