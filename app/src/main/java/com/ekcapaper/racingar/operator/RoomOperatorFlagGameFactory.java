package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.AddressMapService;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class RoomOperatorFlagGameFactory implements RoomOperatorAbstractFactory{
    private final Location location;
    private final Session session;
    private final SocketClient socketClient;
    private final Match match;

    private final Double mapLengthKilometer;
    private final Integer timeLimitSecond;

    @Builder
    public RoomOperatorFlagGameFactory(@NonNull Location location,
                                       @NonNull Session session,
                                       @NonNull SocketClient socketClient,
                                       @NonNull Match match,
                                       @NonNull Double mapLengthKilometer,
                                       @NonNull Integer timeLimitSecond) {
        this.location = location;
        this.session = session;
        this.socketClient = socketClient;
        this.match = match;
        this.mapLengthKilometer = mapLengthKilometer;
        this.timeLimitSecond = timeLimitSecond;
    }

    @Override
    public RoomOperator createRoomOperator() {
        AddressMapService addressMapService = AddressMapClient.getMapAddressService();
        MapRange mapRange = MapRange.calculateMapRange(location,mapLengthKilometer);
        Call<List<AddressDto>> addressDtoListCall = addressMapService.drawMapRangeRandom10(mapRange);
        try {
            Response<List<AddressDto>> addressDtoListResponse = addressDtoListCall.execute();
            if(addressDtoListResponse.isSuccessful()){
                List<AddressDto> addressDtoList = addressDtoListResponse.body();
                List<GameFlag> gameFlagList = addressDtoList.stream()
                        .map((addressDto -> {
                            Location location = new Location("");
                            location.setLatitude(addressDto.getLatitude());
                            location.setLongitude(addressDto.getLongitude());
                            return new GameFlag(location);
                        }))
                        .collect(Collectors.toList());
                return RoomOperatorFlagGame.builder()
                        .gameFlagList(gameFlagList)
                        .match(match)
                        .session(session)
                        .socketClient(socketClient)
                        .timeLimitSecond(timeLimitSecond)
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
