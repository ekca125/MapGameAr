package com.ekcapaper.racingar.operator.maker;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;

public class FlagGameRoomOperatorJoinMaker implements GameRoomOperatorMaker{
    private final Client client;
    private final Session session;
    private Duration timeLimit;
    private MapRange mapRange;
    private GameType gameType;
    //
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session) {
        this.client = client;
        this.session = session;
    }

    @Override
    public GameRoomOperator make() {
        return null;
    }
}
