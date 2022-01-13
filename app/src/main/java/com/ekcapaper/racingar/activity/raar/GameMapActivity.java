package com.ekcapaper.racingar.activity.raar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class GameMapActivity extends AppCompatActivity {
    // location checker
    Timer checkTimer;
    TimerTask endCheckTimerTask;
    LocationRequestSpace locationRequestSpace;
    boolean checkAndUpdateStatus;
    // map
    private GoogleMap mMap;
    private boolean mapReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        initMapFragment();
        Tools.setSystemBarColor(this, R.color.colorPrimary);

        mapReady = false;
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = Tools.configActivityMaps(googleMap);
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(37.7610237, -122.4217785));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(zoomingLocation());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        try {
                            mMap.animateCamera(zoomingLocation());
                        } catch (Exception e) {
                        }
                        return true;
                    }
                });
                mapReady = true;
            }
        });
    }

    private CameraUpdate zoomingLocation() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(37.76496792, -122.42206407), 13);
    }


    public void clickAction(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.map_button:
                Toast.makeText(getApplicationContext(), "Map Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.list_button:
                Toast.makeText(getApplicationContext(), "List Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_button:
                Toast.makeText(getApplicationContext(), "Add Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // location checker
    private void stopCheckAndUpdate() {
        if (checkAndUpdateStatus) {
            checkAndUpdateStatus = false;
            this.checkTimer.cancel();
            this.checkTimer = null;
            this.locationRequestSpace.stopRequest();
            this.locationRequestSpace = null;
        }
    }

    Marker marker = null;

    private void startCheckAndUpdate() {
        if (!checkAndUpdateStatus) {
            checkAndUpdateStatus = true;
            this.locationRequestSpace = new LocationRequestSpace(this);

            this.checkTimer = new Timer();
            this.endCheckTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GameMapActivity.this.runOnUiThread(()->{
                        if(mapReady){
                            locationRequestSpace.getCurrentLocation().ifPresent((location)->{
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 13));
                                if(marker != null){
                                    marker.remove();
                                    marker = null;
                                }
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(),location.getLongitude()))
                                        .title("Marker in Sydney"));
                            });

                        }
                    });
                }
            };
            this.checkTimer.schedule(endCheckTimerTask, 0, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckAndUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCheckAndUpdate();
    }
}