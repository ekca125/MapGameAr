package com.ekcapaper.racingar.operator.maker.newroom;

import com.ekcapaper.racingar.operator.layer.GameRoomClient;
import com.ekcapaper.racingar.operator.maker.make.GameRoomClientMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class GameRoomClientNewMaker extends RoomClientNewMaker implements GameRoomClientMaker {
    public GameRoomClientNewMaker(Client client, Session session) {
        super(client, session);
    }

    @Override
    public GameRoomClient makeGameRoomClient() {
        GameRoomClient gameRoomClient = new GameRoomClient(client, session);
        gameRoomClient.createMatch();
        return gameRoomClient;
    }
}
