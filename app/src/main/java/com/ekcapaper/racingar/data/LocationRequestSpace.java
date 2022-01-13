package com.ekcapaper.racingar.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.Optional;

import lombok.Getter;

public class LocationRequestSpace {
    // 위치
    @Getter
    private Optional<Location> currentLocation;

    // 위치 서비스
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Context context;

    public LocationRequestSpace(Context context){
        this.context = context;

        currentLocation = Optional.empty();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location != null) {
                        LocationRequestSpace.this.currentLocation = Optional.ofNullable(location);
                    }
                }
            }
        };

        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(new LocationRequest(),
                locationCallback,
                Looper.getMainLooper());
    }

    public void stopRequest(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}
