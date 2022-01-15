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
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameRoomGenerateActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 0;
    // 상태
    private GameType gameType;
    private GameType[] gameTypeArray;
    // 관제
    private ThisApplication thisApplication;
    // layout
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;
    private TextInputEditText text_input_time_limit;
    private AutoCompleteTextView dropdown_state;
    private Button button_generate_room;
    // 위치 갱신
    LocationRequestSpace locationRequestSpace;
    
    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);
        // toolbar 초기화
        initToolbar();
        // 상태
        gameType = GameType.GAME_TYPE_FLAG;
        gameTypeArray = GameType.values();
        // 관제
        thisApplication = (ThisApplication) getApplicationContext();
        // 액티비티
        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);
        text_input_time_limit = findViewById(R.id.text_input_time_limit);
        dropdown_state = findViewById(R.id.dropdown_state);
        // 게임 종류를 설정하는 텍스트 입력을 설정
        ArrayAdapter adapter2 = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.stream(gameTypeArray).map(Enum::toString).collect(Collectors.toList())
        );
        dropdown_state.setAdapter(adapter2);
        dropdown_state = findViewById(R.id.dropdown_state);
        dropdown_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gameType = gameTypeArray[i];
            }
        });
        // 이벤트 설정
        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRoomAndMoveRoom();
            }
        });
        // 기본 초기화
        // drop down
        dropdown_state.setText(GameType.GAME_TYPE_FLAG.toString());
        // 생성 버튼
        button_generate_room.setEnabled(false);
        button_generate_room.setText("위치를 가져오는 중..");
        // time limit
        text_input_time_limit.setText("3600");
        // 위치 갱신 시작
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                runOnUiThread(() -> {
                    text_input_latitude.setText(String.valueOf(Math.abs(location.getLatitude())));
                    text_input_longitude.setText(String.valueOf(Math.abs(location.getLongitude())));
                    button_generate_room.setEnabled(true);
                    button_generate_room.setText("방을 생성하기");
                });
            }
        });
    }

    private void generateRoomAndMoveRoom() {
        button_generate_room.setEnabled(false);
        // 데이터 준비
        double latitude = Double.parseDouble(Objects.requireNonNull(text_input_latitude.getText()).toString());
        double longitude = Double.parseDouble(Objects.requireNonNull(text_input_longitude.getText()).toString());
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        // 방 생성 쓰레드 실행
        CompletableFuture.supplyAsync(() -> {
            // 방 만들기
            MapRange mapRange = MapRange.calculateMapRange(location, 1);
            return thisApplication.makeGameRoom(gameType, Duration.ofSeconds(100), mapRange);
        }).thenAccept(result -> {
            // 방을 만든 후에 진행
            GameRoomGenerateActivity.this.runOnUiThread(() -> {
                if (result) {
                    // 중지
                    locationRequestSpace.stop();
                    // 게임 시작 선언
                    thisApplication.getCurrentGameRoomOperator().declareGameStart();
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                button_generate_room.setEnabled(true);
            });
        });
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