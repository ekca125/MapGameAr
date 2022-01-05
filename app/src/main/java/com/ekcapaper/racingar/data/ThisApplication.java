package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.GameRoomOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObjectAcks;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class ThisApplication extends Application {
    private Client client;
    private Session session;
    private GameRoomOperator currentGameRoomOperator;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new DefaultClient(
                KeyStorageNakama.getServerKey(),
                KeyStorageNakama.getGrpcAddress(),
                KeyStorageNakama.getGrpcPort(),
                KeyStorageNakama.getGrpcSSL()
        );
        session = null;
        currentGameRoomOperator = null;
    }

    public void login(String email, String password){
        try {
            session = client.authenticateEmail(email, password).get();
        } catch (ExecutionException | InterruptedException e) {
            session = null;
        }
    }

    public Optional<Session> getSessionOptional() {
        return Optional.ofNullable(session);
    }

    private String convertGameFlagListToJson(List<GameFlag> gameFlagList){
        Gson gson = new Gson();
        return gson.toJson(gameFlagList);
    }

    private List<GameFlag> convertJsonToGameFlagList(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ArrayList<GameFlag>>(){}.getType());
    }

    private String getMatchCollectionName(String matchId){
        return "match-" + matchId;
    }

    private String getGameFlagListName(){
        return "GameFlagList";
    }

    
    // try catch로 정리
    public boolean makeGameFlagRoom(MapRange mapRange, Duration timeLimit){
        // 맵에 해당하는 데이터 받아오기
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

    public boolean joinRoom(){
        // 방에 입장하기

        // 읽어와서 오퍼레이터를 만들기
    }

}
