package com.ekcapaper.racingar.activity.raar;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.LocationRequestSpaceUpdater;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomOperator;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GameMapActivity extends AppCompatActivity {
    // location checker
    LocationRequestSpaceUpdater locationRequestSpaceUpdater;
    // marker
    Optional<Marker> playerMarker;
    // map
    private GoogleMap mMap;
    private boolean mapReady;
    // game room operator
    private ThisApplication thisApplication;
    private GameRoomOperator gameRoomOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        initMapFragment();
        Tools.setSystemBarColor(this, R.color.colorPrimary);

        mapReady = false;

        thisApplication = (ThisApplication) getApplicationContext();
        gameRoomOperator = thisApplication.getCurrentGameRoomOperator();

        locationRequestSpaceUpdater = new LocationRequestSpaceUpdater(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                runOnUiThread(() -> {
                            gameRoomOperator.declareCurrentPlayerMove(location);
                            syncGameMap();
                        }
                );
            }
        });
        // markers
        playerMarker = Optional.empty();
        flagMarkers = new ArrayList<>();
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

    List<Optional<Marker>> flagMarkers;

    private void syncGameMap() {
        if (!mapReady) {
            return;
        }
        if(gameRoomOperator instanceof FlagGameRoomOperator){
            gameRoomOperator.getCurrentPlayer().getLocation().ifPresent(location -> {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                playerMarker.ifPresent(Marker::remove);
                playerMarker = Optional.empty();
                playerMarker = Optional.ofNullable(mMap.addMarker(MarkerFactory.createMarkerOption("player",location)));
                //
                flagMarkers.stream().forEach((optionalMarker)-> optionalMarker.ifPresent(Marker::remove));
                flagMarkers.clear();
                List<GameFlag> gameFlagList = ((FlagGameRoomOperator) gameRoomOperator).getUnownedFlagList();
                gameFlagList.stream().forEach((gameFlag -> {
                    mMap.addMarker(MarkerFactory.createMarkerOption("flag",gameFlag.getLocation()));
                }));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationRequestSpaceUpdater.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationRequestSpaceUpdater.stop();
    }

    static class MarkerFactory{
        public static MarkerOptions createMarkerOption(String type, Location location){
            return createMarkerOption(type,new LatLng(location.getLatitude(),location.getLongitude()));
        }

        public static MarkerOptions createMarkerOption(String type, LatLng latLng){
            if(type.equals("flag")){
                return new MarkerOptions().position(latLng);
                /*
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_cake);
                return new MarkerOptions().position(latLng).icon(icon);

                 */
            }
            else if(type.equals("player")){
                return new MarkerOptions().position(latLng);
            }
            else{
                return new MarkerOptions().position(latLng);
            }
        }
    }
}