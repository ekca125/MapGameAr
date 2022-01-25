package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adaptergame.AdapterGameRoom;
import com.ekcapaper.racingar.data.DataGenerator;
import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.helper.SwipeItemTouchHelper;
import com.ekcapaper.racingar.modelgame.item.GameRoomInfo;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.heroiclabs.nakama.api.GroupUserList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GameRoomActivity extends AppCompatActivity implements ActivityInitializer {
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    private NakamaGameManager nakamaGameManager;
    private GameRoomPlayOperator gameRoomPlayOperator;

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterGameRoom mAdapter;

    private Button button_game_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        initActivity();
        initToolbar();
    }

    @Override
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
        nakamaGameManager = thisApplication.getNakamaGameManager();
        gameRoomPlayOperator = (GameRoomPlayOperator) nakamaGameManager.getRoomOperator();
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
                gameRoomPlayOperator.declareGameStart();
            }
        });
        refreshRoomComponent();
        gameRoomPlayOperator.setAfterGameStartMessage(()->{
            Intent intent = new Intent(getApplicationContext(), GameMapActivity.class);
            startActivity(intent);
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game Room");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
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

    private void refreshRoomComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<GameRoomInfo> items = new ArrayList<>();
        try {
            GroupUserList groupUserList = nakamaGameManager.getGameRoomGroupUserList();
            items = groupUserList.getGroupUsersList()
                    .stream()
                    .map(groupUser -> {
                        return GameRoomInfo.builder()
                                .name(String.valueOf(groupUser.getUser().getId()))
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            items = new ArrayList<>();
            GameRoomInfo obj = new GameRoomInfo("ERROR NO DATA");
            items.add(obj);
        }

        mAdapter = new AdapterGameRoom(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGameRoom.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameRoomInfo obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}