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
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.GameRoomLabel;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.FlagGameRoomClient;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.operator.GameRoomClientFactory;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class GameRoomGenerateActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 0;
    // field
    private ThisApplication thisApplication;
    LocationRequestSpace locationRequestSpace;
    // activity
    private TextInputEditText text_input_name;
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;
    private AutoCompleteTextView dropdown_state;
    private Button button_generate_room;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != ACTIVITY_REQUEST_CODE){
            // 잘못 코딩한 경우에 발생하는 예외
            throw new IllegalStateException();
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);

        // field
        thisApplication = (ThisApplication) getApplicationContext();

        // activity
        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_name = findViewById(R.id.text_input_name);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);
        dropdown_state = findViewById(R.id.dropdown_state);

        // activity setting
        dropdown_state.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.stream(GameType.values()).map(Enum::toString).collect(Collectors.toList())
        ));
        dropdown_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 현재에는 게임타입의 값을 그대로 사용하고 있기 때문에 비어있는 상태이며
                // 게임타입의 값 대신에 다른 문자열을 사용할때 클릭하면 현재의 게임타입을 변화시키도록 만들면 된다.
            }
        });
        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_generate_room.setEnabled(false);
                // 방 이름
                String roomName = Objects.requireNonNull(text_input_name.getText()).toString();
                String roomDesc = "";

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
                String selectGameType = dropdown_state.getText().toString();
                
                // label 정보 준비
                Gson gson = new Gson();
                GameRoomLabel gameRoomLabel = new GameRoomLabel(
                        roomName,
                        roomDesc,
                        MapRange.calculateMapRange(location,1)
                );
                String label = gson.toJson(gameRoomLabel);
                
                // 진행
                button_generate_room.setEnabled(false);
                if(selectGameType.equals(GameType.GAME_TYPE_FLAG.toString())){
                  boolean result = thisApplication.createGameRoom(FlagGameRoomClient.class.getName(),label);
                    if (result) {
                        locationRequestSpace.stop();
                        Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                        startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                    } else {
                        Toast.makeText(GameRoomGenerateActivity.this, "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }



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
        locationRequestSpace.start();
        //
        initToolbar();
        // stub
        String roomName = RandomStringUtils.randomAlphabetic(10);
        text_input_name.setText(roomName);
        // stub 2
        button_generate_room.setEnabled(false);
        button_generate_room.setText(R.string.loading_location);
        dropdown_state.setText(GameType.GAME_TYPE_FLAG.toString());
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