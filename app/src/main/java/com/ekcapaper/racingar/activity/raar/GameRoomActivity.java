package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adaptergame.AdapterGameRoom;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.modelgame.GameRoomLabel;
import com.ekcapaper.racingar.modelgame.item.GameRoomInfo;
import com.ekcapaper.racingar.modelgame.play.GameTypeTextConverter;
import com.ekcapaper.racingar.operator.GameRoomClient;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GameRoomActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 0;
    // field
    private ThisApplication thisApplication;
    private GameRoomClient gameRoomClient;
    // activity
    private View parent_view;
    private RecyclerView recyclerView;
    private TextView room_name;
    private TextView room_desc;
    private TextView room_match_id;
    private TextView room_master_user_id;
    private TextView room_game_type;
    private Button button_game_start;
    // adapter
    private List<GameRoomInfo> mGameRoomItems;
    private AdapterGameRoom mGameRoomAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != ACTIVITY_REQUEST_CODE) {
            // 잘못 코딩한 경우에 발생하는 예외
            throw new IllegalStateException();
        }
        thisApplication.leaveGameRoom();
        finish();
    }

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

        Log.d("gameLabel",gameRoomClient.getGameRoomLabel().getRoomName());
        Log.d("gameLabel", String.valueOf(gameRoomClient.getMatchUserPresenceList().size()));

        // activity
        parent_view = findViewById(android.R.id.content);
        button_game_start = findViewById(R.id.button_game_start);
        room_name = findViewById(R.id.room_name);
        room_desc = findViewById(R.id.room_desc);
        room_match_id = findViewById(R.id.room_match_id);
        room_master_user_id = findViewById(R.id.room_master_user_id);
        room_game_type = findViewById(R.id.room_game_type);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // activity data
        GameRoomLabel gameRoomLabel = gameRoomClient.getGameRoomLabel();
        room_name.setText("방 이름 : " + gameRoomLabel.getRoomName());
        room_desc.setText("방 설명 : " + gameRoomLabel.getRoomDesc());
        room_match_id.setText("매치 ID :" + gameRoomClient.getMatch().getMatchId());
        room_master_user_id.setText("방장 ID : " + gameRoomLabel.getMasterUserId());
        room_game_type.setText("게임 타입 : " + GameTypeTextConverter.convertGameTypeToText(gameRoomLabel.getGameType()));

        // activity setting
        button_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_game_start.setEnabled(false);
                Toast.makeText(GameRoomActivity.this, "게임을 시작합니다.", Toast.LENGTH_SHORT).show();
                CompletableFuture.runAsync(() -> {
                            gameRoomClient.declareGameStart();
                        }
                )
                        .thenRun(() -> {
                            gameRoomClient.setAfterOnMatchPresence(() -> {
                            });
                            button_game_start.setEnabled(true);
                        });
            }
        });
        gameRoomClient.setAfterGameStartMessage(() -> {
            Intent intent = new Intent(getApplicationContext(), GameMapActivity.class);
            startActivityForResult(intent,ACTIVITY_REQUEST_CODE);
        });

        // adapter
        mGameRoomItems = new ArrayList<>();
        mGameRoomAdapter = new AdapterGameRoom(this, mGameRoomItems);
        mGameRoomAdapter.setOnItemClickListener(new AdapterGameRoom.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameRoomInfo obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.userName + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mGameRoomAdapter);
        // get player data
        refreshRoomPlayerList();
        //
        gameRoomClient.setAfterOnMatchPresence(()->{
            runOnUiThread(this::refreshRoomPlayerList);
        });
        //
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.game_room);
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
                            .userName(player.getUsername())
                            .userId(player.getUserId())
                            .build();
                })
                .collect(Collectors.toList());
        this.mGameRoomItems.clear();
        this.mGameRoomItems.addAll(items);
        mGameRoomAdapter.notifyDataSetChanged();
    }
}