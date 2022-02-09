package com.ekcapaper.mapgamear.activity.raar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.data.LocationRequestSpace;
import com.ekcapaper.mapgamear.data.ThisApplication;
import com.ekcapaper.mapgamear.modelgame.GameRoomLabel;
import com.ekcapaper.mapgamear.modelgame.address.MapRange;
import com.ekcapaper.mapgamear.modelgame.play.GameType;
import com.ekcapaper.mapgamear.modelgame.play.GameTypeTextConverter;
import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;
import com.ekcapaper.mapgamear.operator.FlagGameRoomClient;
import com.ekcapaper.mapgamear.operator.TagGameRoomClient;
import com.ekcapaper.mapgamear.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameRoomGenerateActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 0;
    LocationRequestSpace locationRequestSpace;
    // GameType
    GameType[] gameTypes;
    GameType currentGameType;
    // MapSize
    double[] mapSizes;
    double currentMapSize;
    // field
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    // activity
    private TextInputEditText text_input_name;
    private TextInputEditText text_input_room_desc;
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;
    private TextInputEditText text_input_room_time_limit;
    private Spinner game_type_spinner;
    private Spinner game_map_size_spinner;
    private Button button_generate_room;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != ACTIVITY_REQUEST_CODE) {
            // 잘못 코딩한 경우에 발생하는 예외
            throw new IllegalStateException();
        }
        thisApplication.leaveGameRoom();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);

        // field
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();

        // activity
        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_name = findViewById(R.id.text_input_name);
        text_input_room_desc = findViewById(R.id.text_input_room_desc);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);
        text_input_room_time_limit = findViewById(R.id.text_input_room_time_limit);

        game_type_spinner = findViewById(R.id.game_type_spinner);
        game_map_size_spinner = findViewById(R.id.game_map_size_spinner);

        // game type
        gameTypes = GameType.values();
        currentGameType = GameType.GAME_TYPE_FLAG;

        // activity setting
        game_type_spinner.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.stream(gameTypes)
                        .map(GameTypeTextConverter::convertGameTypeToText)
                        .collect(Collectors.toList())
        ));
        game_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentGameType = gameTypes[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mapSizes = new double[]{0.5,1,1.5,2,3.0};
        currentMapSize = mapSizes[0];

        game_map_size_spinner.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.stream(mapSizes)
                .mapToObj(doubleData -> doubleData + "km")
                .collect(Collectors.toList())
        ));
        game_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentMapSize = mapSizes[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_generate_room.setEnabled(false);
                // 방 이름
                String roomName = Objects.requireNonNull(text_input_name.getText()).toString();
                String roomDesc = Objects.requireNonNull(text_input_room_desc.getText()).toString();
                String roomTimeLimit = Objects.requireNonNull(text_input_room_time_limit.getText()).toString();

                int timeLimitSecond = Integer.parseInt(roomTimeLimit);

                // 위치 정보 가져오기
                String latitudeStr = Objects.requireNonNull(text_input_latitude.getText()).toString();
                String longitudeStr = Objects.requireNonNull(text_input_longitude.getText()).toString();
                // 변환
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);
                // 변환 2
                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                // 게임 선택
                GameType gameType = currentGameType;

                // 맵 크기 선택
                double mapSize = currentMapSize;

                // label 정보 준비
                Gson gson = new Gson();
                GameRoomLabel gameRoomLabel = new GameRoomLabel(
                        roomName,
                        roomDesc,
                        MapRange.calculateMapRange(location, mapSize),
                        nakamaNetworkManager.getCurrentSessionUserId(),
                        gameType,
                        true,
                        timeLimitSecond
                );
                String label = gson.toJson(gameRoomLabel);

                // 진행
                button_generate_room.setEnabled(false);
                boolean result = false;
                if (gameType.equals(GameType.GAME_TYPE_FLAG)) {
                    result = thisApplication.createGameRoom(FlagGameRoomClient.class.getName(), label);
                } else if (gameType.equals(GameType.GAME_TYPE_TAG)) {
                    result = thisApplication.createGameRoom(TagGameRoomClient.class.getName(), label);
                } else {
                    throw new IllegalArgumentException();
                }
                // 결과 확인
                if (result) {
                    locationRequestSpace.stop();
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                } else {
                    Toast.makeText(GameRoomGenerateActivity.this, "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                button_generate_room.setEnabled(true);
            }
        });
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                locationRequestSpace.stop();
                runOnUiThread(() -> {
                    text_input_latitude.setText(String.valueOf(Math.abs(location.getLatitude())));
                    text_input_longitude.setText(String.valueOf(Math.abs(location.getLongitude())));
                    button_generate_room.setEnabled(true);
                    button_generate_room.setText(R.string.generate_room);
                });
            }
        });
        button_generate_room.setEnabled(false);
        locationRequestSpace.start();
        //
        currentGameType = GameType.GAME_TYPE_FLAG;
        //
        initToolbar();
        // stub
        String roomName = RandomStringUtils.randomAlphabetic(10);
        text_input_name.setText(roomName);
        // stub
        text_input_room_time_limit.setText("3600");
        // stub 2
        button_generate_room.setEnabled(false);
        button_generate_room.setText(R.string.loading_location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thisApplication.leaveGameRoom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_room_generate, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

}