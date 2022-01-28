package com.ekcapaper.racingar;

import android.location.Location;
import android.util.Log;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageFlagGameStart;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.stub.LocationStub;
import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageParseTest {

    @Test
    public void testParse(){
        Gson gson = new Gson();
        GameMessageStart gameMessageStart = new GameMessageStart();
        String gameMessageStartJson = gson.toJson(gameMessageStart);
        GameMessageStart gameMessageStartResult = gson.fromJson(gameMessageStartJson,GameMessageStart.class);
    }

    @Test
    public void testFlagGameStart(){
        Gson gson = new Gson();
        try {
            MapRange mapRange = MapRange.calculateMapRange(LocationStub.location,1);

            List<AddressDto> addressDtoList = AddressMapClient.getMapAddressService()
                    .drawMapRangeRandom10(mapRange).execute().body();
            List<GameFlag> gameFlagList = addressDtoList.stream().map(addressDto -> {
                Location location = new Location("");
                location.setLatitude(addressDto.getLatitude());
                location.setLongitude(addressDto.getLongitude());
                return new GameFlag(location);
            }).collect(Collectors.toList());
            // 메시지 전송
            GameMessageFlagGameStart gameMessageStart = new GameMessageFlagGameStart(gameFlagList);
            String gameMessageStartJson = gson.toJson(gameMessageStart);
            GameMessageStart gameMessageStartResult = gson.fromJson(gameMessageStartJson,GameMessageFlagGameStart.class);
            Log.d("gamestart",gameMessageStartJson);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
