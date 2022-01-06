package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.operator.maker.GameRoomOperatorMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class GameRoomOperatorJoinMaker implements GameRoomOperatorMaker {
    private final Client client;
    private final Session session;
    private final String matchId;

    public GameRoomOperatorJoinMaker(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = matchId;
    }

    @Override
    public GameRoomOperator makeGameRoomOperator() {
        GameRoomOperator gameRoomOperator = new GameRoomOperator(client, session);
        gameRoomOperator.joinMatch(matchId);
        return gameRoomOperator;
    }
}
