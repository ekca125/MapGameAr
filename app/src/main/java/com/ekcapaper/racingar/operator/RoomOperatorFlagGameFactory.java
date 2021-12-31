package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.AddressMapService;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

public class RoomOperatorFlagGameFactory implements RoomOperatorAbstractFactory{
    private final Location location;
    private final Session session;
    private final SocketClient socketClient;
    private final Match match;

    public RoomOperatorFlagGameFactory(Location location, Session session, SocketClient socketClient, Match match) {
        this.location = location;
        this.session = session;
        this.socketClient = socketClient;
        this.match = match;
    }

    @Override
    public RoomOperator createRoomOperator() {
        AddressMapService addressMapService = AddressMapClient.getMapAddressService();

        return null;
    }
}
