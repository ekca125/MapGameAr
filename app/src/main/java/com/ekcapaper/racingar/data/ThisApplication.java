package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;


public class ThisApplication extends Application {
    @Getter
    NakamaNetworkManager nakamaNetworkManager;
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
    }

}
