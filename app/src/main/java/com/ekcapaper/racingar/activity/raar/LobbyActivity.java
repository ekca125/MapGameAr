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
import com.ekcapaper.racingar.modelgame.item.GameRoomInfo;
import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.utils.Tools;
import com.heroiclabs.nakama.api.GroupList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LobbyActivity extends AppCompatActivity {
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
        mLobbyAdapter = new AdapterLobby(this,mLobbyItems);

        initToolbar();
    }

    private void refreshLobbyData() {
        List<GameLobbyRoomItem> items = new ArrayList<>();
        this.mLobbyItems.clear();
        this.mLobbyItems.addAll(items);
        mLobbyAdapter.notifyDataSetChanged();
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
            /*
            if(locationRefresher.isRunning()){
                Toast.makeText(getApplicationContext(), "이미 방의 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "방의 정보를 다시 가져오고 있습니다.", Toast.LENGTH_SHORT).show();

            }

             */
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}