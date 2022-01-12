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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterLobby;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.model.GameLobbyRoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.utils.Tools;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyActivity extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterLobby mAdapter;

    private Button button_new_room;

    private List<GameLobbyRoomInfo> items;

    // 관제
    private ThisApplication thisApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        parent_view = findViewById(android.R.id.content);
        thisApplication = (ThisApplication) getApplicationContext();

        button_new_room = findViewById(R.id.button_new_room);
        button_new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GameRoomGenerateActivity.class);
                startActivity(intent);
            }
        });

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game Lobby");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        refreshLobby();
    }

    private void refreshLobby() {
        // data
        // items = new ArrayList<>();

        List<RoomInfo> roomInfoList = thisApplication.getCurrentRoomInfo();
        items = roomInfoList.stream()
                .map((roomInfo)->{
                    GameLobbyRoomInfo gameLobbyRoomInfo = new GameLobbyRoomInfo();
                    gameLobbyRoomInfo.gameType = roomInfo.getGameType();
                    // 위치 정보를 가져와서 설정
                    gameLobbyRoomInfo.distanceCenter = "11111";
                    gameLobbyRoomInfo.name = roomInfo.getMatchId();
                    return gameLobbyRoomInfo;
                })
                .collect(Collectors.toList());

        //set data and list adapter
        mAdapter = new AdapterLobby(this, items);
        recyclerView.setAdapter(mAdapter);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameLobbyRoomInfo obj, int position) {
                Intent intent = new Intent(getApplicationContext(),GameRoomActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
            }
        });
        // stub
        //items.addAll(DataGenerator.getGameRoomInfoData(this));
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
        }
        else if(item.getItemId() == R.id.action_refresh){
            refreshLobby();
            Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}