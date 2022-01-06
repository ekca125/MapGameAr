package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.GameRoomClient;
import com.ekcapaper.racingar.operator.layer.RoomClient;
import com.ekcapaper.racingar.operator.maker.GameRoomClientMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class GameRoomClientJoinMaker extends RoomClientJoinMaker implements GameRoomClientMaker {
    public GameRoomClientJoinMaker(Client client, Session session, String matchId) {
        super(client, session, matchId);
    }

    @Override
    public GameRoomClient makeGameRoomClient() {
        GameRoomClient gameRoomClient = new GameRoomClient(client,session);
        gameRoomClient.joinMatch(matchId);
        return gameRoomClient;
    }
}
