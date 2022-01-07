package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.operator.maker.GameRoomOperatorMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class GameRoomOperatorJoinMaker extends GameRoomClientJoinMaker implements GameRoomOperatorMaker {

    public GameRoomOperatorJoinMaker(Client client, Session session, String matchId) {
        super(client, session, matchId);
    }

    @Override
    public GameRoomOperator makeGameRoomOperator() {
        GameRoomOperator gameRoomOperator = new GameRoomOperator(client, session);
        gameRoomOperator.joinMatch(matchId);
        return gameRoomOperator;
    }
}
