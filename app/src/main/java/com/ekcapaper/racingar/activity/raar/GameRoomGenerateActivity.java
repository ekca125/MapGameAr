package com.ekcapaper.racingar.activity.raar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GameRoomGenerateActivity extends AppCompatActivity {
    private TextInputEditText text_input_latitude;
    private TextInputEditText text_input_longitude;

    private Button button_generate_room;
    private ThisApplication thisApplication;

    Timer checkTimer;
    TimerTask endCheckTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room_generate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new String[]{GameType.GAME_TYPE_FLAG.toString()});
        ((AutoCompleteTextView) findViewById(R.id.dropdown_state)).setAdapter(adapter2);

        thisApplication = (ThisApplication) getApplicationContext();

        button_generate_room = findViewById(R.id.button_generate_room);
        text_input_latitude = findViewById(R.id.text_input_latitude);
        text_input_longitude = findViewById(R.id.text_input_longitude);

        button_generate_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GameRoomActivity.class);
                startActivity(intent);
            }
        });
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
        this.checkTimer.cancel();
        this.checkTimer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.checkTimer = new Timer();
        this.endCheckTimerTask = new TimerTask() {
            @Override
            public void run() {
                GameRoomGenerateActivity.this.runOnUiThread(() -> {
                    thisApplication.getCurrentLocation().ifPresent(location -> {
                        text_input_latitude.setText(String.valueOf(Math.abs(location.getLatitude())));
                        text_input_longitude.setText(String.valueOf(Math.abs(location.getLongitude())));
                    });
                });
            }
        };
        this.checkTimer.schedule(endCheckTimerTask,0,1000);
    }
}