package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;

public abstract class RoomOperatorFactory {
    protected Client client;
    protected Session session;
    protected Duration timeLimit;

    public RoomOperatorFactory(Client client, Session session, Duration timeLimit) {
        this.client = client;
        this.session = session;
        this.timeLimit = timeLimit;
    }

    public abstract RoomOperator createRoom(Location location, double mapLengthKilometer);
    public abstract RoomOperator joinRoom(String matchId);
}
