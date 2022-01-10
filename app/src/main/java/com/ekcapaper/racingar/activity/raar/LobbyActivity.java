package com.ekcapaper.racingar.activity.raar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterListAnimation;
import com.ekcapaper.racingar.adapter.AdapterLobby;
import com.ekcapaper.racingar.data.DataGenerator;
import com.ekcapaper.racingar.model.GameRoomInfo;
import com.ekcapaper.racingar.model.People;
import com.ekcapaper.racingar.utils.ItemAnimation;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterLobby mAdapter;
    private List<GameRoomInfo> items = new ArrayList<>();
    private int animation_type = ItemAnimation.BOTTOM_UP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        items = DataGenerator.getGameRoomInfoData(this);
        items.addAll(DataGenerator.getGameRoomInfoData(this));
        items.addAll(DataGenerator.getGameRoomInfoData(this));
        items.addAll(DataGenerator.getGameRoomInfoData(this));
        items.addAll(DataGenerator.getGameRoomInfoData(this));

        showSingleChoiceDialog();
    }

    private void setAdapter() {
        //set data and list adapter
        mAdapter = new AdapterLobby(this, items, animation_type);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterLobby.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GameRoomInfo obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_animation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_refresh:
                setAdapter();
                break;
            case R.id.action_mode:
                showSingleChoiceDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final String[] ANIMATION_TYPE = new String[]{
            "Bottom Up", "Fade In", "Left to Right", "Right to Left"
    };

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Animation Type");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(ANIMATION_TYPE, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selected = ANIMATION_TYPE[i];
                if (selected.equalsIgnoreCase("Bottom Up")) {
                    animation_type = ItemAnimation.BOTTOM_UP;
                } else if (selected.equalsIgnoreCase("Fade In")) {
                    animation_type = ItemAnimation.FADE_IN;
                } else if (selected.equalsIgnoreCase("Left to Right")) {
                    animation_type = ItemAnimation.LEFT_RIGHT;
                } else if (selected.equalsIgnoreCase("Right to Left")) {
                    animation_type = ItemAnimation.RIGHT_LEFT;
                }
                getSupportActionBar().setTitle(selected);
                setAdapter();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}