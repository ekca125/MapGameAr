package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.operator.GameRoomClientFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;


public class ThisApplication extends Application {
    NakamaNetworkManager nakamaNetworkManager;
    @Getter
    GameRoomClient gameRoomClient;
    @Getter
    ExecutorService executorService;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nakamaNetworkManager = new NakamaNetworkManager();
        executorService = Executors.newFixedThreadPool(4);
        gameRoomClient = null;
    }

    public boolean createGameRoom(String clientTypeName, String label){
        if(gameRoomClient != null){
            throw new IllegalStateException();
        }
        gameRoomClient = GameRoomClientFactory.createGameRoomClientNewMatch(clientTypeName,nakamaNetworkManager,label);
        return gameRoomClient != null;
    }

    public boolean joinGameRoom(String clientTypeName, String matchId){
        if(gameRoomClient != null){
            throw new IllegalStateException();
        }
        gameRoomClient = GameRoomClientFactory.createGameRoomClientJoinMatch(clientTypeName,nakamaNetworkManager,matchId);
        return gameRoomClient != null;
    }
}
