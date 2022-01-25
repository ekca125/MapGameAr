package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.ekcapaper.racingar.data.NakamaRoomPreparedDataManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomPlayOperator;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayOperator;
import com.ekcapaper.racingar.utils.Tools;
import com.heroiclabs.nakama.api.GroupList;

import java.time.Duration;
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
    private LocationRequestSpace locationRefresher;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initActivity();
    }

    @Override
    public void initActivityField() {
        // managers
        thisApplication = (ThisApplication) getApplicationContext();
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
        nakamaGameManager = thisApplication.getNakamaGameManager();
        // location
        currentLocation = null;
    }

    @Override
    public void initActivityComponent() {
        parent_view = findViewById(android.R.id.content);
        button_new_room = findViewById(R.id.button_new_room);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAdapter = null;
        items = null;

        initToolbar();
    }

    @Override
    public void initActivityEventTask() {
        button_new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationRefresher.stop();
                Intent intent = new Intent(getApplicationContext(), GameRoomGenerateActivity.class);
                startActivity(intent);
            }
        });
        setLobbyItemLocationMessage();
        // 위치 갱신
        locationRefresher = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                locationRefresher.stop();
                currentLocation = location;
                thisApplication.getExecutorService().submit(()->{
                    updateLobbyData();
                });
            }
        });
    }

    private void setLobbyItemLocationMessage(){
        List<GameLobbyRoomItem> items = new ArrayList<>();
        items.add(new GameLobbyRoomItem("위치를 불러오는 중..","",""));
        updateLobbyView(items);
    }


    private void updateLobbyData(){
        if(currentLocation == null){
            // 반드시 위치를 가져온 이후에 업데이트의 용도로 접근되어야 한다.
            throw new IllegalStateException();
        }
        List<GameLobbyRoomItem> items = new ArrayList<>();
        GroupList groupList = nakamaNetworkManager.getAllGroupList();
        if(groupList == null){
            items.add(new GameLobbyRoomItem("열려있는 방이 없습니다.","",""));
        }
        else{
            items = groupList.getGroupsList().stream()
                    .map(group->{
                        try{
                            NakamaRoomMetaDataManager nakamaRoomMetaDataManager = new NakamaRoomMetaDataManager(nakamaNetworkManager);
                            Map<String, Object> metadata = nakamaRoomMetaDataManager.readRoomMetaDataSync(group);
                            String groupId =(String) metadata.get("groupId");
                            String matchId = (String) metadata.get("matchId");
                            return new GameLobbyRoomItem(group.getName(),groupId,matchId);
                        }
                        catch (Exception e){
                            Log.d("testtest",e.toString());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }
        updateLobbyView(items);
    }

    private void updateLobbyView(List<GameLobbyRoomItem> gameLobbyRoomItems){
        this.runOnUiThread(()->{
            items = gameLobbyRoomItems;
            mAdapter = new AdapterLobby(this, items);
            recyclerView.setAdapter(mAdapter);
            // on item list clicked
            mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
                @Override
                public void onItemClick(View view, GameLobbyRoomItem obj, int position) {
                    NakamaRoomPreparedDataManager nakamaRoomPreparedDataManager = new NakamaRoomPreparedDataManager(nakamaNetworkManager);
                    Map<String,Object> prepareData = nakamaRoomPreparedDataManager.readRoomPrepareDataSync(obj.name);
                    // 변경예정
                    FlagGameRoomPlayOperator flagGameRoomPlayOperator = new FlagGameRoomPlayOperator(nakamaNetworkManager,nakamaGameManager,
                            Duration.ofSeconds(3600)
                            ,(List<GameFlag>) prepareData.get("gameFlagList"));
                    nakamaGameManager.joinGameRoom(obj.name, flagGameRoomPlayOperator);
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivity(intent);
                }
            });
        });
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
            if(locationRefresher.isRunning()){
                Toast.makeText(getApplicationContext(), "이미 방의 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
                locationRefresher.start();
            }
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationRefresher.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationRefresher.stop();
    }
}