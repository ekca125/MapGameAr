package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.RoomClient;
import com.ekcapaper.racingar.operator.maker.RoomClientMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class RoomClientJoinMaker implements RoomClientMaker {
    private final Client client;
    private final Session session;
    private final String matchId;

    public RoomClientJoinMaker(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = matchId;
    }

    @Override
    public RoomClient makeRoomClient() {
        RoomClient roomClient = new RoomClient(client,session);
        roomClient.joinMatch(matchId);
        return roomClient;
    }
}
