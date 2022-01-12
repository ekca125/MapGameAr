package com.ekcapaper.racingar.operator.maker;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;

public class FlagGameRoomOperatorNewMaker implements GameRoomOperatorMaker{
    private final Client client;
    private final Session session;
    private final Duration timeLimit;
    private final MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit, MapRange mapRange) {
        this.client = client;
        this.session = session;
        this.timeLimit = timeLimit;
        this.mapRange = mapRange;
    }

    @Override
    public GameRoomOperator make() {
        return null;
    }
}
