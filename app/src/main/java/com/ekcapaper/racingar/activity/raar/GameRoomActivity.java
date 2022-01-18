package com.ekcapaper.racingar.activity.raar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterGameRoom;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.model.GameRoomInfo;
import com.ekcapaper.racingar.helper.SwipeItemTouchHelper;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperatorDeprecated;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GameRoomActivity extends AppCompatActivity implements ActivityInitializer{
    private ThisApplication thisApplication;
    private GameRoomPlayOperatorDeprecated gameRoomOperator;

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterGameRoom mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private Button button_game_start;
    private Timer refreshTimer;
    private TimerTask refreshTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        initActivity();
        initToolbar();
        refreshRoomComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game Room");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void refreshRoomComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //List<GameRoomInfo> items = DataGenerator.getGameRoomInfo(this);
        List<GameRoomInfo> items = gameRoomOperator.getPlayerList().stream().map(player->{
            GameRoomInfo obj = new GameRoomInfo();
            obj.image = R.drawable.image_2;
            obj.name = String.valueOf(player.getUserId());
            obj.imageDrw = this.getResources().getDrawable(obj.image);
            return obj;
        }).collect(Collectors.toList());

        //set data and list adapter
        mAdapter = new AdapterGameRoom(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGameRoom.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameRoomInfo obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.Callback callback = new SwipeItemTouchHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
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
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
        gameRoomOperator = thisApplication.getCurrentGameRoomOperator();
        refreshTimer = new Timer();
        refreshTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    refreshRoomComponent();
                });
            }
        };

    }

    @Override
    public void initActivityComponent() {
        parent_view = findViewById(android.R.id.content);
        button_game_start = findViewById(R.id.button_game_start);
    }

    @Override
    public void initActivityEventTask() {
        button_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GameMapActivity.class);
                startActivity(intent);
            }
        });
        refreshTimer.schedule(refreshTimerTask,0,100);
    }
}