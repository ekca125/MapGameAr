package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.reader.RoomInfoReader;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorNewMaker;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;

public class ThisApplication extends Application {
    private Client client;
    private Session session;

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
            Log.d("RoomINFO",match.getMatchId());
            RoomInfoReader roomInfoReader = new RoomInfoReader(client, session, RoomDataSpace.normalizeMatchId(match.getMatchId()));
            RoomInfo roomInfo = roomInfoReader.readRoomInfo();
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

}
