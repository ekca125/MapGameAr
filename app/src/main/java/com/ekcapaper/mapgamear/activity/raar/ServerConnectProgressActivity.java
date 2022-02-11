package com.ekcapaper.mapgamear.activity.raar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.data.ThisApplication;
import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;
import com.ekcapaper.mapgamear.utils.Tools;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerConnectProgressActivity extends AppCompatActivity {
    private final int ACTIVITY_REQUEST_CODE = 2;
    // manager
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    //
    private ProgressBar progress_indeterminate_circular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connect_progress);

        this.thisApplication = (ThisApplication) getApplicationContext();
        this.nakamaNetworkManager = this.thisApplication.getNakamaNetworkManager();

        initToolbar();
        initComponent();

        runConnectServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        progress_indeterminate_circular = findViewById(R.id.progress_indeterminate_circular);
        runProgressDeterminateCircular();
    }

    private void runConnectServer() {
        CompletableFuture
                .supplyAsync(() -> {
                    // 접속 ID
                    String guestId;
                    // 이전에 접속한 ID의 존재여부의 확인 및 저장, 불러오기
                    SharedPreferences sharedPreferences = getPreferences(Activity.MODE_PRIVATE);
                    String guestIdKey = getString(R.string.preference_key_guest_id);
                    if (sharedPreferences.contains(guestIdKey)) {
                        // 이전에 접속한 ID가 있는 경우
                        guestId = sharedPreferences.getString(guestIdKey, "");
                    } else {
                        // 이전에 접속한 ID가 없는 경우
                        guestId = UUID.randomUUID().toString();
                        // 접속에 사용될 ID를 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(guestIdKey, guestId);
                        editor.commit();
                    }
                    return nakamaNetworkManager.loginGuestSync(guestId);
                })
                .thenAccept((result) -> {
                    runOnUiThread(() -> {
                        if (result) {
                            Intent intent = new Intent(ServerConnectProgressActivity.this, LobbyActivity.class);
                            startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                });
    }

    private void runProgressDeterminateCircular() {
        final Handler mHandler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                int progress = progress_indeterminate_circular.getProgress() + 10;
                progress_indeterminate_circular.setProgress(progress);
                if (progress > 100) {
                    progress_indeterminate_circular.setProgress(0);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty, menu);
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
}
