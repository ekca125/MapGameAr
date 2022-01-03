package com.ekcapaper.racingar.operator;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public abstract class RoomOperatorFactory {
    Client client;
    Session session;

    public RoomOperatorFactory(Client client, Session session) {
        this.client = client;
        this.session = session;
    }

    public abstract RoomOperator createRoom();
    public abstract RoomOperator joinRoom(String matchId);
}
