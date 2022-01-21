package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.Rpc;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Response;

public class ThisApplication extends Application {
    NakamaNetworkManager nakamaNetworkManager;
    NakamaGameManager nakamaGameManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nakamaNetworkManager = new NakamaNetworkManager();
        nakamaGameManager = new NakamaGameManager(nakamaNetworkManager);
    }

}
