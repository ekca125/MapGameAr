package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import lombok.Getter;


public class ThisApplication extends Application {
    @Getter
    NakamaNetworkManager nakamaNetworkManager;
    @Getter
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
