package com.ekcapaper.racingar.activity.raar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.operator.FlagGameRoomClient;
import com.ekcapaper.racingar.operator.GameRoomClient;
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
    LocationRequestSpace locationRequestSpace;
    // marker
    Optional<Marker> playerMarker;
    List<Marker> flagMarkers;
    // map
    private GoogleMap mMap;
    private boolean mapReady;
    // game room operator
    private ThisApplication thisApplication;
    private GameRoomClient gameRoomClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);
        // field
        mapReady = false;
        playerMarker = Optional.empty();
        flagMarkers = new ArrayList<>();

        thisApplication = (ThisApplication) getApplicationContext();
        gameRoomClient = thisApplication.getGameRoomClient();
        // activity
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                runOnUiThread(() -> {
                            gameRoomClient.declareCurrentPlayerMove(location);
                            syncGameMap();
                        }
                );
            }
        });
        initMapFragment();
        Tools.setSystemBarColor(this, R.color.colorPrimary);
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

    private void syncGameMap() {
        if (!mapReady) {
            return;
        }
        MarkerFactory markerFactory = new MarkerFactory(GameMapActivity.this);
/*
        if (gameRoomClient instanceof FlagGameRoomClient) {
            gameRoomClient.getGamePlayerList().stream()
                    .filter(player)

                    .getCurrentPlayer().getLocation().ifPresent(location -> {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                playerMarker.ifPresent(Marker::remove);
                playerMarker = Optional.empty();
                playerMarker = Optional.ofNullable(mMap.addMarker(markerFactory.createMarkerOption("player", location)));
                //
                flagMarkers.forEach(Marker::remove);
                flagMarkers.clear();
                List<GameFlag> gameFlagList = ((FlagGameRoomPlayOperator) gameRoomOperator).getUnownedFlagList();
                gameFlagList.forEach((gameFlag -> {
                    flagMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("flag", gameFlag.getLocation())));
                }));
            });
        }

 */
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationRequestSpace.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationRequestSpace.stop();
    }

    static class MarkerFactory {
        Context context;

        public MarkerFactory(Context context) {
            this.context = context;
        }

        private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        public MarkerOptions createMarkerOption(String type, Location location) {
            return createMarkerOption(type, new LatLng(location.getLatitude(), location.getLongitude()));
        }

        public MarkerOptions createMarkerOption(String type, LatLng latLng) {
            if (type.equals("flag")) {
                //return new MarkerOptions().position(latLng);
                BitmapDescriptor icon = bitmapDescriptorFromVector(context, R.drawable.ic_cake);
                return new MarkerOptions().position(latLng).icon(icon);
            } else if (type.equals("player")) {
                return new MarkerOptions().position(latLng);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}