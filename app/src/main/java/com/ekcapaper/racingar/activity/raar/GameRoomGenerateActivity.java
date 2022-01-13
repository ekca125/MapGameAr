package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.graphics.PorterDuff;
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
import java.util.Timer;
import java.util.TimerTask;

public class GameRoomGenerateActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 0;
    Timer checkTimer;
    TimerTask endCheckTimerTask;
    LocationRequestSpace locationRequestSpace;
    boolean checkAndUpdateStatus;
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;
    private AutoCompleteTextView dropdown_state;
    private Button button_generate_room;
    private ThisApplication thisApplication;
    private GameType gameType;
    private GameType[] gameTypeArray;
    private String[] gameTypeStringArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        // 상태
        checkAndUpdateStatus = false;
        gameType = GameType.GAME_TYPE_FLAG;
        // 액티비티
        thisApplication = (ThisApplication) getApplicationContext();
        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);
        // 게임 종류를 확인하는 어댑터 설정
        gameTypeArray = GameType.values();
        gameTypeStringArray = new String[gameTypeStringArray.length];
        for(int i=0;i<gameTypeArray.length;i++){
            gameTypeStringArray[i] = gameTypeArray[i].toString();
        }

        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, gameTypeStringArray);
        ((AutoCompleteTextView) findViewById(R.id.dropdown_state)).setAdapter(adapter2);
        dropdown_state = ((AutoCompleteTextView) findViewById(R.id.dropdown_state));
        dropdown_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gameType = gameTypeArray[i];
            }
        });

        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRoomAndMoveRoom();
            }
        });
    }

    private void generateRoomAndMoveRoom(){
        locationRequestSpace.getCurrentLocation().ifPresent(location -> {
            MapRange mapRange = MapRange.calculateMapRange(location,1);
            boolean result = thisApplication.makeGameRoom(gameType, Duration.ofSeconds(100), mapRange);
            if(result){
                stopCheckAndUpdate();
                Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
            }
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
        stopCheckAndUpdate();
    }

    private void stopCheckAndUpdate() {
        if (checkAndUpdateStatus) {
            checkAndUpdateStatus = false;
            this.checkTimer.cancel();
            this.checkTimer = null;
            this.locationRequestSpace.stopRequest();
            this.locationRequestSpace = null;
        }
    }

    private void startCheckAndUpdate() {
        if (!checkAndUpdateStatus) {
            checkAndUpdateStatus = true;
            this.locationRequestSpace = new LocationRequestSpace(this);

            this.checkTimer = new Timer();
            this.endCheckTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GameRoomGenerateActivity.this.runOnUiThread(() -> {
                        locationRequestSpace.getCurrentLocation().ifPresent(location -> {
                            text_input_latitude.setText(String.valueOf(Math.abs(location.getLatitude())));
                            text_input_longitude.setText(String.valueOf(Math.abs(location.getLongitude())));
                        });
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
}