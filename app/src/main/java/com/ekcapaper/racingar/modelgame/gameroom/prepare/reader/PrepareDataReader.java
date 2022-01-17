package com.ekcapaper.racingar.modelgame.gameroom.prepare.reader;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class PrepareDataReader {
    protected final Client client;
    protected final Session session;
    protected final String matchId;

    public PrepareDataReader(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = RoomDataSpace.normalizeMatchId(matchId);
    }
}
