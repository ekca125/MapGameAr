package com.ekcapaper.racingar.operator.maker.newroom;

import android.location.Location;
import android.util.Log;

import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfoFlagGame;
import com.ekcapaper.racingar.operator.maker.make.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.modelgame.gameroom.writer.RoomInfoWriter;
import com.ekcapaper.racingar.modelgame.gameroom.writer.RoomPrepareDataWriter;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class FlagGameRoomOperatorNewMaker extends TimeLimitGameRoomOperatorNewMaker implements FlagGameRoomOperatorMaker, RoomInfoWriter, RoomPrepareDataWriter {
    private final MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit, MapRange mapRange) {
        super(client, session, timeLimit);
        this.mapRange = mapRange;
        this.gameFlagList = null;
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
    public boolean writeRoomInfo(String matchId) {
        // util
        Gson gson = new Gson();
        // collection
        String collectionName = RoomDataSpace.getCollectionName(matchId);
        RoomInfoFlagGame roomInfoFlagGame = new RoomInfoFlagGame(timeLimit.getSeconds(), GameType.GAME_TYPE_FLAG, mapRange);

        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                RoomDataSpace.getDataRoomInfoKey(),
                gson.toJson(roomInfoFlagGame),
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("test", e.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean writeRoomPrepareData(String matchId) {
        // util
        Gson gson = new Gson();
        // collection
        String collectionName = RoomDataSpace.getCollectionName(matchId);
        PrepareDataFlagGameRoom roomInfoFlagGame = new PrepareDataFlagGameRoom(gameFlagList);

        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                RoomDataSpace.getDataRoomPrepareKey(),
                gson.toJson(roomInfoFlagGame),
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("test", e.toString());
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        // 맵 받아오기
        boolean result = requestGameFlagList(mapRange);
        if (!result) {
            return null;
        }
        // 방 만들기
        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, timeLimit, gameFlagList);
        boolean matchProcessSuccess = flagGameRoomOperator.createMatch();
        if (!matchProcessSuccess) {
            return null;
        }
        // 방 데이터 쓰기
        Match match = flagGameRoomOperator.getMatch().get();
        String matchId = match.getMatchId();
        boolean writeSuccess = writeRoomInfo(matchId) && writeRoomPrepareData(matchId);
        if (!writeSuccess) {
            flagGameRoomOperator.leaveMatch();
            return null;
        }
        return flagGameRoomOperator;
    }


}
