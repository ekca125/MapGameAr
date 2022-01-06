package com.ekcapaper.racingar.operator.maker.newroom;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.operator.layer.RoomClient;
import com.ekcapaper.racingar.operator.maker.RoomClientMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class RoomClientNewMaker implements RoomClientMaker {
    private final Client client;
    private final Session session;

    public RoomClientNewMaker(Client client, Session session) {
        this.client = client;
        this.session = session;
    }

    @Override
    public RoomClient makeRoomClient() {
        RoomClient roomClient = new RoomClient(client,session);
        roomClient.createMatch();
        return roomClient;
    }
}
