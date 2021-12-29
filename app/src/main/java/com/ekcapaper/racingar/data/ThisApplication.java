package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ThisApplication extends Application {
    private Client client;
    private Optional<Session> session;

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
                true
        );
        session = Optional.empty();
    }

    public void login(String email, String password){
        try {
            session = Optional.ofNullable(client.authenticateEmail(email, password).get());
        } catch (ExecutionException | InterruptedException e) {
            session = Optional.empty();
        }
    }

    public Optional<Session> getSession() {
        return session;
    }
}
