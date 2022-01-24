package com.ekcapaper.racingar.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ekcapaper.racingar.stub.LocationStub;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import lombok.Getter;

public class LocationRequestSpace {
    // 정보
    private Context context;
    // 위치 서비스
    LocationManager locationManager;
    LocationListener locationListener;
    // running 여부
    @Getter
    boolean running;
    
    public LocationRequestSpace(Context context, Consumer<Location> runFunction) {
        this.context = context;

        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = runFunction::accept;

        this.running = false;
    }

    public void start(){
        if(running){
            // 이미 실행되고 있는 경우
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, Looper.getMainLooper());
        running = true;
    }

    public void stop(){
        if(!running){
            // 실행되지 않은 경우
            return;
        }
        locationManager.removeUpdates(locationListener);
        running = false;
    }
}
