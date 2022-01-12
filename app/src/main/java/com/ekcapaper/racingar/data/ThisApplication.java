package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.operator.maker.SaveDataNameDefine;
import com.ekcapaper.racingar.operator.maker.data.RoomInfoFlagGame;
import com.ekcapaper.racingar.operator.maker.joinroom.FlagGameRoomOperatorJoinMaker;
import com.ekcapaper.racingar.operator.maker.newroom.FlagGameRoomOperatorNewMaker;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.MatchList;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;

public class ThisApplication extends Application {
    private Client client;
    private Session session;
    private GameRoomOperator currentGameRoomOperator;

    @Getter
    private ExecutorService executorService;


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
        executorService = Executors.newFixedThreadPool(4);
    }

    public void login(String email, String password) {
        try {
            session = client.authenticateEmail(email, password).get();
        } catch (ExecutionException | InterruptedException e) {
            session = null;
        }
    }

    public Optional<Session> getSessionOptional() {
        return Optional.ofNullable(session);
    }

    public boolean createFlagGameRoom(Duration timeLimit, MapRange mapRange) {
        FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client, session, timeLimit, mapRange);
        FlagGameRoomOperator flagGameRoomOperator = flagGameRoomOperatorNewMaker.makeFlagGameRoomOperator();
        if (flagGameRoomOperator != null) {
            this.currentGameRoomOperator = flagGameRoomOperator;
            return true;
        } else {
            return false;
        }
    }

    public boolean joinFlagGameRoom(String matchId) {
        FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client, session, matchId);
        FlagGameRoomOperator flagGameRoomOperator = flagGameRoomOperatorJoinMaker.makeFlagGameRoomOperator();
        if (flagGameRoomOperator != null) {
            this.currentGameRoomOperator = flagGameRoomOperator;
            return true;
        } else {
            return false;
        }
    }

    public MatchList getCurrentMatches() {
        try {
            return client.listMatches(session).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public List<RoomInfoFlagGame> getCurrentMatchesInfo() {
        MatchList matchList = getCurrentMatches();
        if (matchList == null) {
            return null;
        }
        List<RoomInfoFlagGame> roomInfoFlagGameList = matchList.getMatchesList().stream().map(match -> {
            Gson gson = new Gson();

            StorageObjectId storageObjectId = new StorageObjectId(SaveDataNameDefine.getCollectionName(match.getMatchId()));
            storageObjectId.setKey(SaveDataNameDefine.getDataRoomInfoKey());
            storageObjectId.setUserId(session.getUserId());

            StorageObjects storageObjectsMapRange = null;
            try {
                storageObjectsMapRange = client.readStorageObjects(session, storageObjectId).get();
                StorageObject storageObjectMapRange = storageObjectsMapRange.getObjects(0);
                RoomInfoFlagGame roomInfoFlagGame = gson.fromJson(storageObjectMapRange.getValue(), RoomInfoFlagGame.class);
                return roomInfoFlagGame;
            } catch (ExecutionException | InterruptedException e) {
                return null;
            }
        })
                .collect(Collectors.toList());
        return roomInfoFlagGameList;
    }

}
