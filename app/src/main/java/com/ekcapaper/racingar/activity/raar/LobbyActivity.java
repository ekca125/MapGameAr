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
import com.ekcapaper.racingar.adaptergame.AdapterLobby;
import com.ekcapaper.racingar.data.LocationRequestSpace;
import com.ekcapaper.racingar.data.NakamaNetworkManager;
import com.ekcapaper.racingar.data.NakamaRoomMetaDataManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.utils.Tools;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.val;

public class LobbyActivity extends AppCompatActivity implements ActivityInitializer {
    // 관제
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    // activity component
    private View parent_view;
    private RecyclerView recyclerView;
    private Button button_new_room;
    // data
    private AdapterLobby mAdapter;
    private List<GameLobbyRoomItem> items;
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
        nakamaNetworkManager = thisApplication.getNakamaNetworkManager();
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
            Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
            refreshRequester.start();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLobbyData(Location location) {
        items = new ArrayList<>();
        try {
            GroupList groupList = nakamaNetworkManager.getAllGroupList();
            List<GameLobbyRoomItem> groupItems = groupList.getGroupsList().stream()
                    .map((Group group) -> {
                        NakamaRoomMetaDataManager nakamaRoomMetaDataManager = new NakamaRoomMetaDataManager(nakamaNetworkManager);
                        Map<String,Object> roomMetaData = nakamaRoomMetaDataManager.readRoomMetaDataSync(group);
                        return GameLobbyRoomItem.builder()
                                .groupId((String) roomMetaData.get("groupId"))
                                .matchId((String) roomMetaData.get("matchId"))
                                .build();
                    })
                    .collect(Collectors.toList());
            items.addAll(groupItems);
        } catch (NullPointerException ignored) {

        }

        mAdapter = new AdapterLobby(this, items);
        recyclerView.setAdapter(mAdapter);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameLobbyRoomItem obj, int position) {
                // 입장기능은 맵까지 완성시키고 나서 돌아와서 구현
                Toast.makeText(getApplicationContext(), "미구현된 내용입니다.", Toast.LENGTH_SHORT).show();
                /*
                // join room
                // join group 을 한다.

                if (thisApplication.(obj.gameType, obj.matchId)) {
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "방의 입장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                 */
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