package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;

public class ThisApplication extends Application {
    Client client;
    Session session;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

}
