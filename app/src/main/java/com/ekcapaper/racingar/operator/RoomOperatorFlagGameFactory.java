package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.AddressMapService;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

import java.util.List;

import retrofit2.Call;

public class RoomOperatorFlagGameFactory implements RoomOperatorAbstractFactory{
    private final Location location;
    private final Session session;
    private final SocketClient socketClient;
    private final Match match;

    private final double mapLengthKilometer;

    public RoomOperatorFlagGameFactory(Location location,
                                       Session session,
                                       SocketClient socketClient,
                                       Match match,
                                       double mapLengthKilometer) {
        this.location = location;
        this.session = session;
        this.socketClient = socketClient;
        this.match = match;
        this.mapLengthKilometer = mapLengthKilometer;
    }

    @Override
    public RoomOperator createRoomOperator() {
        AddressMapService addressMapService = AddressMapClient.getMapAddressService();
        MapRange mapRange = MapRange.calculateMapRange(location,mapLengthKilometer);
        Call<List<AddressDto>> listCall = addressMapService.drawMapRangeRandom10(mapRange);
        listCall.execute();

        return null;
    }
}
