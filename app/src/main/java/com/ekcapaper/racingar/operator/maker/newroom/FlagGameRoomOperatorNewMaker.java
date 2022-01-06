package com.ekcapaper.racingar.operator.maker.newroom;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
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

public class FlagGameRoomOperatorNewMaker extends TimeLimitGameRoomOperatorNewMaker implements FlagGameRoomOperatorMaker {
    private final MapRange mapRange;

    public FlagGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit, MapRange mapRange) {
        super(client, session, timeLimit);
        this.mapRange = mapRange;
    }

    List<GameFlag> requestGameFlagList(MapRange mapRange) {
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
        try {
            Response<List<AddressDto>> response = requester.execute();
            if (!response.isSuccessful()) {
                return null;
            }
            List<AddressDto> addressDtoList = response.body();
            List<GameFlag> gameFlagList = addressDtoList.stream().map(addressDto -> {
                Location location = new Location("");
                location.setLatitude(addressDto.getLatitude());
                location.setLongitude(addressDto.getLongitude());
                return new GameFlag(location);
            }).collect(Collectors.toList());
            return gameFlagList;
        } catch (IOException e) {
            return null;
        }
    }

    boolean writeGameFlagList(String matchId, List<GameFlag> gameFlagList) {
        // util
        Gson gson = new Gson();
        // data
        String collectionName = ServerRoomSaveDataNameSpace.getCollectionName(matchId);
        String keyName = ServerRoomSaveDataNameSpace.getGameFlagListName();
        String jsonGameFlagList = gson.toJson(gameFlagList);
        // write
        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                keyName,
                jsonGameFlagList,
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        List<GameFlag> gameFlagList = requestGameFlagList(mapRange);
        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, timeLimit, gameFlagList);
        boolean matchProcessSuccess = flagGameRoomOperator.createMatch();
        if (!matchProcessSuccess) {
            return null;
        }
        Match match = flagGameRoomOperator.getMatch().get();
        String matchId = match.getMatchId();

        boolean writeSuccess = writeGameFlagList(matchId, gameFlagList);
        if (!writeSuccess) {
            return null;
        }

        return flagGameRoomOperator;
    }
}