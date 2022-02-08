package com.ekcapaper.mapgamear.activity.raar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.data.LocationRequestSpace;
import com.ekcapaper.mapgamear.data.ThisApplication;
import com.ekcapaper.mapgamear.modelgame.play.GameFlag;
import com.ekcapaper.mapgamear.modelgame.play.Player;
import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;
import com.ekcapaper.mapgamear.operator.FlagGameRoomClient;
import com.ekcapaper.mapgamear.operator.GameRoomClient;
import com.ekcapaper.mapgamear.operator.TagGameRoomClient;
import com.ekcapaper.mapgamear.utils.Tools;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GameMapActivity extends AppCompatActivity {
    // map marker
    List<Marker> playerMarkers;
    List<Marker> flagMarkers;
    List<Marker> taggerMarkers;
    // location checker
    LocationRequestSpace locationRequestSpace;
    // field
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    private GameRoomClient gameRoomClient;
    // activity
    private ImageButton list_button;
    private ImageButton map_button;
    private ImageButton add_button;
    private TextView textview_left_time;
    private Timer leftTimeTimer;
    // map
    private GoogleMap mMap;
    private boolean mapReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        // field
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
        gameRoomClient = thisApplication.getGameRoomClient();
        if (gameRoomClient == null) {
            throw new IllegalStateException();
        }

        // activity
        list_button = findViewById(R.id.list_button);
        map_button = findViewById(R.id.map_button);
        add_button = findViewById(R.id.add_button);
        textview_left_time = findViewById(R.id.textview_left_time);
        textview_left_time.setText(gameRoomClient.getLeftTimeStr());

        leftTimeTimer = new Timer();
        leftTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                textview_left_time.setText(gameRoomClient.getLeftTimeStr());
            }
        }, 0, 1000);

        // field
        mapReady = false;
        playerMarkers = new ArrayList<>();
        flagMarkers = new ArrayList<>();
        taggerMarkers = new ArrayList<>();

        // activity
        initMapFragment();

        // activity setting
        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "List Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Map Clicked", Toast.LENGTH_SHORT).show();
                moveCameraMapCenter();
            }
        });
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Add Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        // location refresh
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                runOnUiThread(() -> {
                            gameRoomClient.declareCurrentPlayerMove(location);
                        }
                );
            }
        });
        //
        gameRoomClient.setAfterMovePlayerMessage(()->{
            runOnUiThread(this::syncGameMap);
        });
        gameRoomClient.setAfterOnMatchPresence(()->{
            runOnUiThread(this::syncGameMap);
        });
        gameRoomClient.setAfterGameEndMessage(() -> {
            runOnUiThread(() -> {
                if (gameRoomClient instanceof FlagGameRoomClient) {
                    FlagGameRoomClient flagGameRoomClient = (FlagGameRoomClient) gameRoomClient;
                    flagGameRoomClient.getGamePlayerList().stream()
                            .reduce((playerA, playerB) -> {
                                int playerAPoint = flagGameRoomClient.getPoint(playerA.getUserId());
                                int playerBPoint = flagGameRoomClient.getPoint(playerB.getUserId());
                                if (playerAPoint < playerBPoint) {
                                    return playerB;
                                } else {
                                    return playerA;
                                }
                            })
                            .ifPresent(player -> {
                                if (player.getUserId().equals(nakamaNetworkManager.getCurrentSessionUserId())) {
                                    Toast.makeText(getApplicationContext(), "승리했습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "패배했습니다..", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else if (gameRoomClient instanceof TagGameRoomClient) {
                    TagGameRoomClient tagGameRoomClient = (TagGameRoomClient) gameRoomClient;
                    Player taggerPlayer = tagGameRoomClient.getCurrentTaggerPlayer();
                    if (taggerPlayer.getUserId().equals(nakamaNetworkManager.getCurrentSessionUserId())) {
                        // 게임이 끝나는 시점에서 술래인 경우
                        Toast.makeText(getApplicationContext(), "패배했습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "승리했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove callback
        gameRoomClient.setAfterGameStartMessage(()->{});
        gameRoomClient.setAfterMovePlayerMessage(()->{});
        gameRoomClient.setAfterGameEndMessage(()->{});
        // leave
        thisApplication.leaveGameRoom();
        leftTimeTimer.cancel();
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = Tools.configActivityMaps(googleMap);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setPadding(0, 0, 0, 100);
                mapReady = true;
                //
                moveCameraMapCenter();
                syncGameMap();
            }
        });
    }

    private void moveCameraMapCenter() {
        Location mapCenter = gameRoomClient.getGameRoomLabel().getMapCenter();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapCenter.getLatitude(), mapCenter.getLongitude()), 13));
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
                    player.getLocation().ifPresent(location -> {
                        playerMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("player", location)));
                    });
                });
        if (gameRoomClient instanceof FlagGameRoomClient) {
            flagMarkers.forEach(Marker::remove);
            flagMarkers.clear();
            List<GameFlag> gameFlagList = ((FlagGameRoomClient) gameRoomClient).getUnownedFlagList();
            gameFlagList.forEach((gameFlag -> {
                flagMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("flag", gameFlag.getLocation())));
            }));
        }
        if (gameRoomClient instanceof TagGameRoomClient) {
            // 술래
            taggerMarkers.forEach(Marker::remove);
            taggerMarkers.clear();
            Player taggerPlayer = ((TagGameRoomClient) gameRoomClient).getCurrentTaggerPlayer();
            taggerPlayer.getLocation().ifPresent(location -> {
                taggerMarkers.add(mMap.addMarker(markerFactory.createMarkerOption("tagger", location)));
            });
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
            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            return BitmapDescriptorFactory.fromBitmap(resizeBitmap);
        }

        public MarkerOptions createMarkerOption(String type, Location location) {
            return createMarkerOption(type, new LatLng(location.getLatitude(), location.getLongitude()));
        }

        public MarkerOptions createMarkerOption(String type, LatLng latLng) {
            if (type.equals("flag")) {
                //return new MarkerOptions().position(latLng);
                BitmapDescriptor icon = bitmapDescriptorFromVector(context, R.drawable.ic_copper_card);
                return new MarkerOptions().position(latLng).icon(icon);
            } else if (type.equals("tagger")) {
                BitmapDescriptor icon = bitmapDescriptorFromVector(context, R.drawable.ic_copper_card);
                return new MarkerOptions().position(latLng).icon(icon);
            } else if (type.equals("player")) {
                return new MarkerOptions().position(latLng);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}