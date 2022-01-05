package com.ekcapaper.racingar.data;

import android.app.Application;
import android.content.Context;

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
    // operator


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

    public boolean makeRoom(type){
        // 방을 만드는 방법

        // 데이터를 쓰기

        // joinRoom
    }

    public boolean joinRoom(){  
        // 방에 입장하기

        // 읽어와서 오퍼레이터를 만들기
    }

}
