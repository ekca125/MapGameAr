package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.AddressMapService;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class RoomOperatorFlagGameFactory extends RoomOperatorFactory {
    public RoomOperatorFlagGameFactory(Client client, Session session, Duration timeLimit) {
        super(client, session, timeLimit);
    }

    @Override
    public RoomOperator createRoom(Location playerLocation, double mapLengthKilometer) {
        AddressMapService addressMapService = AddressMapClient.getMapAddressService();
        MapRange mapRange = MapRange.calculateMapRange(playerLocation, mapLengthKilometer);
        Call<List<AddressDto>> addressDtoListCall = addressMapService.drawMapRangeRandom10(mapRange);
        try {
            Response<List<AddressDto>> addressDtoListResponse = addressDtoListCall.execute();
            if (addressDtoListResponse.isSuccessful()) {
                List<AddressDto> addressDtoList = addressDtoListResponse.body();
                List<GameFlag> gameFlagList = addressDtoList.stream()
                        .map((addressDto -> {
                            Location location = new Location("");
                            location.setLatitude(addressDto.getLatitude());
                            location.setLongitude(addressDto.getLongitude());
                            return new GameFlag(location);
                        }))
                        .collect(Collectors.toList());
                // 스토리지 엔진에 쓰기


                return RoomOperatorFlagGame.builder()
                        .gameFlagList(gameFlagList)
                        .client(client)
                        .session(session)
                        .timeLimit(timeLimit)
                        .gameFlagList(gameFlagList)
                        .build();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            return null;
        }
        return null;
    }

    @Override
    public RoomOperator joinRoom(String matchId) {
        return null;
    }
}
