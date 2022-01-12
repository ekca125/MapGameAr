package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterListAnimation;
import com.ekcapaper.racingar.adapter.AdapterLobby;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.model.GameLobbyRoomInfo;
import com.ekcapaper.racingar.data.DataGenerator;
import com.ekcapaper.racingar.model.People;
import com.ekcapaper.racingar.operator.maker.SaveDataNameDefine;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterLobby mAdapter;

    private List<GameLobbyRoomInfo> items;

    // 관제
    private ThisApplication thisApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        parent_view = findViewById(android.R.id.content);
        thisApplication = (ThisApplication) getApplicationContext();

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
        items = new ArrayList<>();

        MatchList matchList = thisApplication.getCurrentMatches();
        if(matchList != null){
            List<Match> matches = matchList.getMatchesList();
            matches.stream().forEach((match -> {
                String matchId = match.getMatchId();
                String collectionName = SaveDataNameDefine.getCollectionName(matchId);
                String roomInfoKey = SaveDataNameDefine.getDataRoomInfoKey();




            }));
        }


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
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}