package com.ekcapaper.racingar.operator.maker;

import android.location.Location;

import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.writer.RoomInfoWriter;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.writer.PrepareDataFlagGameRoomWriter;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class FlagGameRoomOperatorNewMaker implements GameRoomOperatorMaker{
    private final Client client;
    private final Session session;
    private final Duration timeLimit;
    private final MapRange mapRange;
    private final GameType gameType;
    //
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit, MapRange mapRange) {
        this.client = client;
        this.session = session;
        this.timeLimit = timeLimit;
        this.mapRange = mapRange;
        this.gameType = GameType.GAME_TYPE_FLAG;
    }

    boolean requestGameFlagList(MapRange mapRange) {
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
        try {
            Response<List<AddressDto>> response = requester.execute();
            if (!response.isSuccessful()) {
                gameFlagList = null;
                return false;
            }
            List<AddressDto> addressDtoList = response.body();
            List<GameFlag> gameFlagList = addressDtoList.stream().map(addressDto -> {
                Location location = new Location("");
                location.setLatitude(addressDto.getLatitude());
                location.setLongitude(addressDto.getLongitude());
                return new GameFlag(location);
            }).collect(Collectors.toList());
            this.gameFlagList = gameFlagList;
        } catch (IOException e) {
            gameFlagList = null;
            return false;
        }
        return true;
    }

    @Override
    public GameRoomPlayOperator make() {
        boolean result;

        result = requestGameFlagList(mapRange);
        if(!result){
            return null;
        }

        FlagGameRoomPlayOperator flagGameRoomOperator = new FlagGameRoomPlayOperator(client,session,timeLimit,gameFlagList);
        result = flagGameRoomOperator.createMatch();
        if(!result){
            return null;
        }

        try {
            String matchId = RoomDataSpace.normalizeMatchId(flagGameRoomOperator.getMatch().getMatchId());

            RoomInfoWriter roomInfoWriter = new RoomInfoWriter(client, session, matchId);
            PrepareDataFlagGameRoomWriter prepareDataFlagGameRoomWriter = new PrepareDataFlagGameRoomWriter(client,session,matchId);
            result = roomInfoWriter.writeRoomInfo(new RoomInfo(timeLimit.getSeconds(), gameType, mapRange, matchId))
                    && prepareDataFlagGameRoomWriter.writePrepareData(new PrepareDataFlagGameRoom(gameFlagList));
        }
        catch (NullPointerException e){
            result = false;
        }
        if(!result){
            flagGameRoomOperator.leaveMatch();
            return null;
        }

        return flagGameRoomOperator;
    }
}
