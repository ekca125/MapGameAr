package com.ekcapaper.racingar.operator.maker.newroom;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.operator.maker.GameRoomOperatorMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class GameRoomOperatorNewMaker implements GameRoomOperatorMaker {
    private final Client client;
    private final Session session;

    public GameRoomOperatorNewMaker(Client client, Session session) {
        this.client = client;
        this.session = session;
    }

    @Override
    public GameRoomOperator makeGameRoomOperator() {
        GameRoomOperator gameRoomOperator = new GameRoomOperator(client, session);
        gameRoomOperator.createMatch();
        return gameRoomOperator;
    }
}
