package com.ekcapaper.racingar.activity.raar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.stream.Collectors;

public class GameMapActivity extends AppCompatActivity {
    // field
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    private GameRoomClient gameRoomClient;
    // map
    private GoogleMap mMap;
    private boolean mapReady;
    // map marker
    List<Marker> playerMarkers;
    List<Marker> flagMarkers;

    // location checker
    LocationRequestSpace locationRequestSpace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        // field
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager= thisApplication.getNakamaNetworkManager();
        gameRoomClient = thisApplication.getGameRoomClient();
        if(gameRoomClient == null){
            throw new IllegalStateException();
        }

        // field
        mapReady = false;
        playerMarkers = new ArrayList<>();
        flagMarkers = new ArrayList<>();

        // activity
        initMapFragment();

        // location refresh
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
        //
        Tools.setSystemBarColor(this, R.color.colorPrimary);
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

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = Tools.configActivityMaps(googleMap);
                mapReady = true;
                Location mapCenter = gameRoomClient.getGameRoomLabel().getMapCenter();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapCenter.getLatitude(), mapCenter.getLongitude()), 13));
            }
        });
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
        // 플레이어들의 위치 마커 변경
        playerMarkers.stream().forEach(Marker::remove);
        playerMarkers.clear();
        gameRoomClient.getGamePlayerList().stream()
                .forEach(player -> {
                    player.getLocation().ifPresent(location ->{
                        playerMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("player",location)));
                    });
                });
        if(gameRoomClient instanceof FlagGameRoomClient){
            flagMarkers.forEach(Marker::remove);
            List<GameFlag> gameFlagList = ((FlagGameRoomClient) gameRoomClient).getUnownedFlagList();
            gameFlagList.forEach((gameFlag -> {
                Log.d("gameFlag",gameFlag.getLocation().toString());
                flagMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("flag", gameFlag.getLocation())));
            }));
        }
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