package com.ekcapaper.racingar.modelgame.gameroom.prepare.writer;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class PrepareDataWriter {
    protected final Client client;
    protected final Session session;
    protected final String matchId;
    //
    protected final String collectionName;
    protected final String keyName;

    public PrepareDataWriter(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = RoomDataSpace.normalizeMatchId(matchId);
        //
        this.collectionName = RoomDataSpace.getCollectionName(matchId);
        this.keyName = RoomDataSpace.getDataRoomPrepareKey();
    }
}
