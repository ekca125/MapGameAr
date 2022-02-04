package com.ekcapaper.mapgamear.activity.raar;

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

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.adaptergame.AdapterLobby;
import com.ekcapaper.mapgamear.data.LocationRequestSpace;
import com.ekcapaper.mapgamear.data.ThisApplication;
import com.ekcapaper.mapgamear.modelgame.GameRoomLabel;
import com.ekcapaper.mapgamear.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.mapgamear.modelgame.play.GameTypeTextConverter;
import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;
import com.ekcapaper.mapgamear.operator.FlagGameRoomClient;
import com.ekcapaper.mapgamear.utils.Tools;
import com.google.gson.Gson;
import com.heroiclabs.nakama.api.MatchList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LobbyActivity extends AppCompatActivity {
    // util
    Gson gson;
    // field
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    // activity
    private View parent_view;
    private RecyclerView recyclerView;
    private Button button_new_room;
    // adapter
    private AdapterLobby mLobbyAdapter;
    private List<GameLobbyRoomItem> mLobbyItems;
    // location
    private LocationRequestSpace locationRequestSpace;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // field
        this.thisApplication = (ThisApplication) getApplicationContext();
        this.nakamaNetworkManager = thisApplication.getNakamaNetworkManager();

        // activity
        parent_view = findViewById(android.R.id.content);
        button_new_room = findViewById(R.id.button_new_room);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // activity setting
        button_new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GameRoomGenerateActivity.class);
                startActivity(intent);
            }
        });

        // adapter
        mLobbyItems = new ArrayList<>();
        mLobbyAdapter = new AdapterLobby(this, mLobbyItems);
        recyclerView.setAdapter(mLobbyAdapter);
        mLobbyAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameLobbyRoomItem obj, int position) {
                // 입장 처리
                // 존재하며 아직 시작되지 않은 방인지 확인
                GameRoomLabel gameRoomLabel = nakamaNetworkManager.getGameRoomLabel(obj.matchId);
                if(gameRoomLabel == null){
                    Toast.makeText(LobbyActivity.this, "존재하지 않는 방입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!gameRoomLabel.isOpened()){
                    Toast.makeText(LobbyActivity.this, "이미 시작된 방입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //
                boolean result = thisApplication.joinGameRoom(FlagGameRoomClient.class.getName(), obj.matchId);
                if (result) {
                    Intent intent = new Intent(getApplicationContext(), GameRoomActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LobbyActivity.this, "방 입장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //
        initToolbar();
        // location
        currentLocation = null;
        locationRequestSpace = new LocationRequestSpace(this, new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                locationRequestSpace.stop();
                LobbyActivity.this.currentLocation = location;
                refreshLobbyData();
            }
        });
        locationRequestSpace.start();
        //

        Toast.makeText(LobbyActivity.this, "방의 목록을 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
        //util
        gson = new Gson();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thisApplication.logout();
    }

    private void refreshLobbyData() {
        if (currentLocation == null) {
            throw new IllegalStateException();
        }

        // 필터링 및 변환 작업
        MatchList matchList = nakamaNetworkManager.getMinPlayerAllMatchListSync();
        if (matchList != null) {
            List<GameLobbyRoomItem> items = matchList.getMatchesList().stream()
                    .filter(match -> {
                                String label = match.getLabel().getValue();
                                GameRoomLabel gameRoomLabel = gson.fromJson(label, GameRoomLabel.class);
                                return gameRoomLabel.isOpened();
                            }
                    )
                    .map(match -> {
                        String label = match.getLabel().getValue();
                        GameRoomLabel gameRoomLabel = gson.fromJson(label, GameRoomLabel.class);
                        double distanceMeter = gameRoomLabel.getMapCenter().distanceTo(currentLocation);
                        return GameLobbyRoomItem.builder()
                                .roomName(gameRoomLabel.getRoomName())
                                .roomDesc(gameRoomLabel.getRoomDesc())
                                .distanceCenter(distanceMeter + "m")
                                .matchId(match.getMatchId())
                                .gameTypeDesc(GameTypeTextConverter.convertGameTypeToText(gameRoomLabel.getGameType()))
                                .build();
                    })
                    .collect(Collectors.toList());
            this.mLobbyItems.clear();
            this.mLobbyItems.addAll(items);
            mLobbyAdapter.notifyDataSetChanged();
        }
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
        getMenuInflater().inflate(R.menu.menu_robby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            thisApplication.logout();
            finish();
        } else if (item.getItemId() == R.id.action_refresh) {
            if (currentLocation == null) {
                Toast.makeText(getApplicationContext(), "위치 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();
                refreshLobbyData();
            }
        } else if(item.getItemId() == R.id.action_logout){
            thisApplication.logout();
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}