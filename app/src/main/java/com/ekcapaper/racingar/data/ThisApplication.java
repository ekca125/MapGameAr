package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.info.reader.RoomInfoReader;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;

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
    private GameRoomOperator currentGameRoomOperator;

    @Getter
    private ExecutorService executorService;

    // current location - 앱이 실행되는 동안에 계속해서 변경
    private FusedLocationProviderClient fusedLocationProviderClient;
    public void startLocationApp(){
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

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
        fusedLocationProviderClient = null;
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


    public MatchList getCurrentMatches() {
        try {
            return client.listMatches(session).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public List<RoomInfo> getCurrentRoomInfo(){
        MatchList matchList = getCurrentMatches();
        if(matchList == null){
            return new ArrayList<>();
        }
        return matchList.getMatchesList().stream()
                .map((match)->{
                    RoomInfoReader roomInfoReader = new RoomInfoReader(client,session,match.getMatchId());
                    return roomInfoReader.readRoomInfo();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
