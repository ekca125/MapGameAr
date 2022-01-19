package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterLobby;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.model.GameLobbyRoomInfo;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LobbyActivity extends AppCompatActivity implements ActivityInitializer {
    // 관제
    private ThisApplication thisApplication;
    // activity component
    private View parent_view;
    private RecyclerView recyclerView;
    private Button button_new_room;
    // data
    private AdapterLobby mAdapter;
    private List<GameLobbyRoomInfo> items;
    // location
    private LocationRequestSpace refreshRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initActivity();
    }

    @Override
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
        mAdapter = null;
        items = null;
    }

    @Override
    public void initActivityComponent() {
        parent_view = findViewById(android.R.id.content);
        button_new_room = findViewById(R.id.button_new_room);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        initToolbar();
    }

    @Override
    public void initActivityEventTask() {
        button_new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshRequester.stop();
                Intent intent = new Intent(getApplicationContext(), GameRoomGenerateActivity.class);
                startActivity(intent);
            }
        });
        refreshRequester = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                updateLobbyData(location);
                updateLobby();
                refreshRequester.stop();
            }
        });
        refreshRequester.stop();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.lobby_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
            refreshRequester.start();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLobbyData(Location location) {
        items = new ArrayList<>();
        /*
        List<RoomInfo> roomInfoList = thisApplication.getCurrentRoomInfo();
        items = roomInfoList.stream()
                .map((roomInfo) -> {
                    GameLobbyRoomInfo gameLobbyRoomInfo = new GameLobbyRoomInfo();
                    gameLobbyRoomInfo.gameType = roomInfo.getGameType();
                    // 위치 정보를 가져와서 설정
                    gameLobbyRoomInfo.distanceCenter = String.valueOf(roomInfo.getMapRange().getMapCenter().distanceTo(location)) + "m";
                    gameLobbyRoomInfo.name = roomInfo.getMatchId();
                    gameLobbyRoomInfo.matchId = roomInfo.getMatchId();
                    return gameLobbyRoomInfo;
                })
                .collect(Collectors.toList());*/
    }

    private void updateLobby() {
        //set data and list adapter
        mAdapter = new AdapterLobby(this, items);
        recyclerView.setAdapter(mAdapter);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameLobbyRoomInfo obj, int position) {
                // join room
                if (thisApplication.joinGameRoom(obj.gameType, obj.matchId)) {
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "방의 입장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRequester.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshRequester.stop();
    }
}