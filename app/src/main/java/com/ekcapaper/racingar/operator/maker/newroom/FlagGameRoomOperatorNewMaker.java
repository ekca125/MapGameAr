package com.ekcapaper.racingar.operator.maker.newroom;

import android.location.Location;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataName;
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
import com.heroiclabs.nakama.api.StorageObjectAcks;

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

    private List<GameFlag> makeGameFlagList(MapRange mapRange) throws IOException, IllegalStateException {
        List<AddressDto> addressDtoList;
        try {
            Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
            Response<List<AddressDto>> response = requester.execute();
            if(!response.isSuccessful()){
                throw new IllegalStateException();
            }
            addressDtoList = response.body();
            assert addressDtoList != null;
            if(addressDtoList.size() <= 0){
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            throw e;
        }
        return addressDtoList.stream().map(addressDto -> {
            Location location = new Location("");
            location.setLatitude(addressDto.getLatitude());
            location.setLongitude(addressDto.getLongitude());
            return new GameFlag(location);
        }).collect(Collectors.toList());
    }

    private boolean writeGameFlagList(String matchId, List<GameFlag> gameFlagList){
        Gson gson = new Gson();

        String collectionName = ServerRoomSaveDataName.getCollectionName(matchId);
        String keyData = ServerRoomSaveDataName.getGameFlagList();
        String gameFlagListJson = gson.toJson(gameFlagList);

        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                keyData,
                gameFlagListJson,
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            StorageObjectAcks acks = client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        try {
            List<GameFlag> gameFlagList = makeGameFlagList(mapRange);
            FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client,session,timeLimit, gameFlagList);
            Match match = flagGameRoomOperator.getMatch().get();
            writeGameFlagList(match.getMatchId(),gameFlagList);
            return flagGameRoomOperator;
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }
}
