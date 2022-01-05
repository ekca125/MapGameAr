package com.ekcapaper.racingar.operator.factory;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObjectAcks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class FlagGameRoomOperatorFactory extends GameRoomOperatorFactory{
    private String convertGameFlagListToJson(List<GameFlag> gameFlagList){
        Gson gson = new Gson();
        return gson.toJson(gameFlagList);
    }

    private List<GameFlag> convertJsonToGameFlagList(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ArrayList<GameFlag>>(){}.getType());
    }


    @Override
    public GameRoomOperator createRoom() {
        List<AddressDto> addressDtoList;
        try {
            Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
            Response<List<AddressDto>> response = requester.execute();
            if(!response.isSuccessful()){
                return false;
            }
            addressDtoList = response.body();
            assert addressDtoList != null;
            if(addressDtoList.size() <= 0){
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        List<GameFlag> gameFlagList = addressDtoList.stream().map(addressDto -> {
            Location location = new Location("");
            location.setLatitude(addressDto.getLatitude());
            location.setLongitude(addressDto.getLongitude());
            return new GameFlag(location);
        }).collect(Collectors.toList());

        //
        if(session == null){
            return false;
        }
        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, timeLimit, gameFlagList);
        boolean success = flagGameRoomOperator.createMatch();
        if(!success){
            return false;
        }

        // 맵 데이터를 쓰기
        Match match = flagGameRoomOperator.getMatch().get();
        String matchId = match.getMatchId();

        String collectionName = getMatchCollectionName(matchId);
        String collectionKey = getGameFlagListName();
        String gameFlagListJson = convertGameFlagListToJson(gameFlagList);

        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                collectionKey,
                gameFlagListJson,
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            StorageObjectAcks acks = client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }

        // 시작
        currentGameRoomOperator = flagGameRoomOperator;
        return true;
    }

    @Override
    public GameRoomOperator joinRoom(String matchId) {
        return null;
    }
}
