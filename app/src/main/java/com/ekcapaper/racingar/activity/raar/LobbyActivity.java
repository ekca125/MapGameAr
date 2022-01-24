package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.ekcapaper.racingar.adaptergame.AdapterLobby;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.data.NakamaGameManager;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.NakamaRoomMetaDataManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.racingar.utils.Tools;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LobbyActivity extends AppCompatActivity implements ActivityInitializer {
    private ThisApplication thisApplication;
    // managers
    private NakamaNetworkManager nakamaNetworkManager;
    private NakamaGameManager nakamaGameManager;
    // activity component
    private View parent_view;
    private RecyclerView recyclerView;
    private Button button_new_room;
    // data
    private AdapterLobby mAdapter;
    private List<GameLobbyRoomItem> items;
    // location service
    private LocationRequestSpace refreshRequester;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initActivity();
    }

    @Override
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
        nakamaGameManager = thisApplication.getNakamaGameManager();
        //
        mAdapter = null;
        items = null;
        //
        currentLocation = null;
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
        // item 초기화
        List<GameLobbyRoomItem> items = new ArrayList<>();
        items.add(new GameLobbyRoomItem("방의 정보를 불러오는 중..","",""));
        updateLobbyView(items);

        // 로비 갱신
        refreshRequester = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                updateLocation(location);
                updateLobbyData();
                refreshRequester.stop();
            }
        });
        refreshRequester.start();
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
            if(refreshRequester.isRunning()){
                Toast.makeText(getApplicationContext(), "이미 방의 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
                refreshRequester.start();
            }
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLocation(Location location){
        this.currentLocation = location;
    }

    private void updateLobbyData(){
        if(currentLocation == null){
            // 반드시 위치를 가져온 이후에 업데이트의 용도로 접근되어야 한다.
            throw new IllegalStateException();
        }
        List<GameLobbyRoomItem> items = new ArrayList<>();
        items.add(new GameLobbyRoomItem("test","",""));
        items.add(new GameLobbyRoomItem("test","",""));
        items.add(new GameLobbyRoomItem("test","",""));
        items.add(new GameLobbyRoomItem("test","",""));
        items.add(new GameLobbyRoomItem("test","",""));
    }

    private void updateLobbyView(List<GameLobbyRoomItem> gameLobbyRoomItems){
        items = gameLobbyRoomItems;
        mAdapter = new AdapterLobby(this, items);
        recyclerView.setAdapter(mAdapter);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameLobbyRoomItem obj, int position) {

            }
        });
    }

/*
    private void updateLobbyData(Location location) {
        List<GameLobbyRoomItem> items = new ArrayList<>();
        try {
            GroupList groupList = nakamaNetworkManager.getAllGroupList();
            groupList.getGroupsList().stream().forEach((group -> {
                Log.d("group",group.getId());
            }));

            items = groupList.getGroupsList().stream()
                    .map()
                    .collect(Collectors.toList());
        } catch (NullPointerException ignored) {
            items.add(new GameLobbyRoomItem("현재 열려있는 방이 없습니다.", "",""));
        }
        this.items = items;
        runOnUiThread(()->{
            mAdapter = new AdapterLobby(this, this.items);
            recyclerView.setAdapter(mAdapter);
            // on item list clicked
            mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
                @Override
                public void onItemClick(View view, GameLobbyRoomItem obj, int position) {
                    Toast.makeText(getApplicationContext(), "미구현된 내용입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

 */

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