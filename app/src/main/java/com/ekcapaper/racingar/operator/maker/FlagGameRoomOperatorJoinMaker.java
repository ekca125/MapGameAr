package com.ekcapaper.racingar.operator.maker;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.reader.RoomInfoReader;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.reader.PrepareDataFlagGameRoomReader;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.time.Duration;
import java.util.List;

public class FlagGameRoomOperatorJoinMaker implements GameRoomOperatorMaker{
    private final Client client;
    private final Session session;
    private final String matchId;
    //
    private Duration timeLimit;
    private MapRange mapRange;
    private GameType gameType;
    //
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = matchId;
    }

    @Override
    public GameRoomPlayOperator make() {
        RoomInfoReader roomInfoReader = new RoomInfoReader(client,session,matchId);
        RoomInfo roomInfo = roomInfoReader.readRoomInfo();
        if(roomInfo == null){
            return null;
        }
        else{
            timeLimit = Duration.ofSeconds(roomInfo.getTimeLimitSeconds());
            mapRange = roomInfo.getMapRange();
            gameType = roomInfo.getGameType();
        }

        PrepareDataFlagGameRoomReader prepareDataFlagGameRoomReader = new PrepareDataFlagGameRoomReader(client,session,matchId);
        PrepareDataFlagGameRoom prepareDataFlagGameRoom =  prepareDataFlagGameRoomReader.readPrepareData();
        if(prepareDataFlagGameRoom == null){
            return null;
        }
        else{
            gameFlagList = prepareDataFlagGameRoom.getGameFlagList();
        }

        FlagGameRoomPlayOperator flagGameRoomOperator = new FlagGameRoomPlayOperator(client,session,timeLimit,gameFlagList);
        // 개발이 완료된 후에 2개의 기기로 테스트한다.
        /*
        boolean result = flagGameRoomOperator.joinMatch(matchId);
        if(!result){
            return null;
        }
        */
        return flagGameRoomOperator;
    }
}
