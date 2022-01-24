package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class GameRoomGenerateActivity extends AppCompatActivity implements ActivityInitializer {
    private final int ACTIVITY_REQUEST_CODE = 0;
    // 위치 갱신
    LocationRequestSpace locationRequestSpace;
    // 상태
    private GameType gameType;
    private GameType[] gameTypeArray;
    // 관제
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    private NakamaGameManager nakamaGameManager;
    // layout
    private TextInputEditText text_input_name;
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;
    private TextInputEditText text_input_time_limit;
    private AutoCompleteTextView dropdown_state;
    private Button button_generate_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);
        initActivity();
        initToolbar();
        // stub
        String roomName = RandomStringUtils.randomAlphabetic(10);
        String roomDesc = "";
        text_input_name.setText(roomName);
    }

    @Override
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
        nakamaGameManager = thisApplication.getNakamaGameManager();
        gameType = GameType.GAME_TYPE_FLAG;
        gameTypeArray = GameType.values();
    }

    @Override
    public void initActivityComponent() {
        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_name = findViewById(R.id.text_input_name);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);
        text_input_time_limit = findViewById(R.id.text_input_time_limit);
        dropdown_state = findViewById(R.id.dropdown_state);

        button_generate_room.setEnabled(false);
        button_generate_room.setText(R.string.loading_location);
        text_input_time_limit.setText("3600");
        dropdown_state.setText(GameType.GAME_TYPE_FLAG.toString());
    }

    @Override
    public void initActivityEventTask() {
        dropdown_state.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.stream(gameTypeArray).map(Enum::toString).collect(Collectors.toList())
        ));
        dropdown_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gameType = gameTypeArray[i];
            }
        });
        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRoomAndJoinRoom();
            }
        });
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                runOnUiThread(() -> {
                    text_input_latitude.setText(String.valueOf(Math.abs(location.getLatitude())));
                    text_input_longitude.setText(String.valueOf(Math.abs(location.getLongitude())));
                    button_generate_room.setEnabled(true);
                    button_generate_room.setText(R.string.generate_room);
                });
            }
        });
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

    private void generateFlagGameRoom(String roomName, String roomDesc, Location mapCenterLocation, Duration timeLimit){
        CompletableFuture.supplyAsync(()-> {
            MapRange mapRange = MapRange.calculateMapRange(mapCenterLocation, 1);
            Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
            try {
                Response<List<AddressDto>> response = requester.execute();
                if (!response.isSuccessful()) {
                    return false;
                }
                List<AddressDto> addressDtoList = response.body();
                List<GameFlag> gameFlagList = addressDtoList.stream()
                        .map(addressDto -> {
                            Location flagLocation = new Location("");
                            flagLocation.setLatitude(addressDto.getLatitude());
                            flagLocation.setLongitude(addressDto.getLongitude());
                            return new GameFlag(flagLocation);
                        })
                        .collect(Collectors.toList());
                FlagGameRoomPlayOperator flagGameRoomPlayOperator = new FlagGameRoomPlayOperator(
                        nakamaNetworkManager,
                        nakamaGameManager,
                        timeLimit,
                        gameFlagList);
                nakamaGameManager.createGameRoom(roomName, roomDesc, flagGameRoomPlayOperator);
            } catch (IOException e) {
                return false;
            }
            return true;
        }).thenAccept(result ->{
            GameRoomGenerateActivity.this.runOnUiThread(() -> {
                if (result) {
                    locationRequestSpace.stop();
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                button_generate_room.setEnabled(true);
            });
        });
    }

    private void generateRoomAndJoinRoom() {
        button_generate_room.setEnabled(false);
        // 정보 불러오기
        String latitudeStr = Objects.requireNonNull(text_input_latitude.getText()).toString();
        String longitudeStr = Objects.requireNonNull(text_input_longitude.getText()).toString();
        String nameStr = Objects.requireNonNull(text_input_name.getText()).toString();
        String timeLimitStr = Objects.requireNonNull(text_input_time_limit.getText()).toString();
        String selectGameType = dropdown_state.getText().toString();
        // 정보 변환
        Location location = new Location("");
        location.setLatitude(Double.parseDouble(latitudeStr));
        location.setLongitude(Double.parseDouble(longitudeStr));
        Duration timeLimit = Duration.ofSeconds(Integer.parseInt(timeLimitStr));
        String desc = "";

        if(selectGameType.equals(GameType.GAME_TYPE_FLAG.toString())){
           generateFlagGameRoom(nameStr,desc,location,timeLimit);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        locationRequestSpace.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationRequestSpace.start();
    }
}