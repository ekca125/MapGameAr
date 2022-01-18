package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.reader.RoomInfoReader;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorJoinMaker;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorNewMaker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;
import com.heroiclabs.nakama.api.Rpc;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;

// group 가입으로 변경

public class ThisApplication extends Application {
    private Client client;
    private Session session;
    private Group group;
    @Getter
    private GameRoomPlayOperator currentGameRoomOperator;

    @Getter
    private ExecutorService executorService;
    //
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

    public boolean login(String email, String password) {
        try {
            session = client.authenticateEmail(email, password).get();
            return true;
        } catch (ExecutionException | InterruptedException e) {
            session = null;
            return false;
        }
    }

    public void loginEmail(String email, String password, FutureCallback<Session> futureCallback) {
        val future = client.authenticateEmail(email, password);
        Futures.addCallback(future, new FutureCallback<Session>() {
            @Override
            public void onSuccess(@Nullable Session result) {
                ThisApplication.this.session = result;
            }

            @Override
            public void onFailure(Throwable t) {
                session = null;
            }
        }, executorService);
        Futures.addCallback(future,futureCallback,executorService);
    }

    public Optional<Session> getSessionOptional() {
        return Optional.ofNullable(session);
    }


    public MatchList getCurrentMatches() throws ExecutionException, InterruptedException {
        return client.listMatches(session).get();
    }

    public List<RoomInfo> getCurrentRoomInfo() {
        MatchList matchList = null;
        try {
            matchList = getCurrentMatches();
        }
        catch (ExecutionException | InterruptedException e) {
            return null;
        }
        List<Match> matches =  matchList.getMatchesList();
        List<RoomInfo> roomInfoList = new ArrayList<>();
        for(Match match:matches){
            String matchId = RoomDataSpace.normalizeMatchId(match.getMatchId());
            RoomInfoReader roomInfoReader = new RoomInfoReader(client, session, matchId);
            Log.d("RoomINFO",matchId);
            RoomInfo roomInfo = roomInfoReader.readRoomInfo();
            if(roomInfo == null){
                Log.d("RoomINFO","roominfo is null");
            }
            roomInfoList.add(roomInfo);
        }
        return roomInfoList;
    }

    public boolean makeGameRoom(GameType gameType, Duration timeLimit, MapRange mapRange) {
        switch (gameType) {
            case GAME_TYPE_FLAG:
                FlagGameRoomOperatorNewMaker flagGameRoomOperatorNewMaker = new FlagGameRoomOperatorNewMaker(client, session, timeLimit, mapRange);
                currentGameRoomOperator = flagGameRoomOperatorNewMaker.make();
                return currentGameRoomOperator != null;
            default:
                return false;
        }
    }

    public boolean joinGameRoom(GameType gameType, String matchId) {
        switch (gameType) {
            case GAME_TYPE_FLAG:
                FlagGameRoomOperatorJoinMaker flagGameRoomOperatorJoinMaker = new FlagGameRoomOperatorJoinMaker(client, session, matchId);
                currentGameRoomOperator = flagGameRoomOperatorJoinMaker.make();
                return currentGameRoomOperator != null;
            default:
                return false;
        }
    }

}
