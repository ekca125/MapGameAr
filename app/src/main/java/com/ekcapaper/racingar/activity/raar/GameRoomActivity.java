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
import com.ekcapaper.racingar.adaptergame.AdapterGameRoom;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.item.GameRoomInfo;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameRoomActivity extends AppCompatActivity {
    // field
    private ThisApplication thisApplication;
    private GameRoomClient gameRoomClient;
    // activity
    private View parent_view;
    private RecyclerView recyclerView;
    private Button button_game_start;
    // adapter
    private List<GameRoomInfo> mGameRoomItems;
    private AdapterGameRoom mGameRoomAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        // field
        thisApplication = (ThisApplication) getApplicationContext();
        gameRoomClient = thisApplication.getGameRoomClient();
        if (gameRoomClient == null) {
            throw new IllegalStateException();
        }

        // activity
        parent_view = findViewById(android.R.id.content);
        button_game_start = findViewById(R.id.button_game_start);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // activity setting
        button_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameRoomClient.setAfterOnMatchPresence(()->{});
                

                gameRoomClient.declareGameStart();
            }
        });
        gameRoomClient.setAfterGameStartMessage(() -> {
            Intent intent = new Intent(getApplicationContext(), GameMapActivity.class);
            startActivity(intent);
        });

        // adapter
        mGameRoomItems = new ArrayList<>();
        mGameRoomAdapter = new AdapterGameRoom(this, mGameRoomItems);
        mGameRoomAdapter.setOnItemClickListener(new AdapterGameRoom.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameRoomInfo obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mGameRoomAdapter);
        // get player data
        refreshRoomPlayerList();
        //
        gameRoomClient.setAfterOnMatchPresence(this::refreshRoomPlayerList);
        //
        initToolbar();
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

    private void refreshRoomPlayerList() {
        List<GameRoomInfo> items = gameRoomClient.getMatchUserPresenceList().stream()
                .map(player -> {
                    return GameRoomInfo.builder()
                            .name(player.getUserId())
                            .build();
                })
                .collect(Collectors.toList());
        this.mGameRoomItems.clear();
        this.mGameRoomItems.addAll(items);
        mGameRoomAdapter.notifyDataSetChanged();
    }
}