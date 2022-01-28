package com.ekcapaper.racingar.activity.raar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.utils.Tools;

/*
    SetupAppActivity 의 흐름
    1. 권한이 존재하는지 확인한다.
    2. 권한이 있다면 로그인 액티비티로 이동한다.
    3. 권한이 없다면 권한을 요청하고 사용자에게 권한을 받으면 로그인 액티비티로 이동한다.
    4. 권한을 거부당하면 앱을 종료한다.
*/

public class SetupAppActivity extends AppCompatActivity {
    // request codes
    private final int PERMISSION_REQUEST_CODE = 1;
    private final int ACTIVITY_REQUEST_CODE = 2;
    // activity component
    TextView textView_setup_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_app);
        // activity component
        textView_setup_app = findViewById(R.id.textView_setup_app);
        // activity 설정
        initToolbar();
        // 권한을 확인한 후에 실행
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startAppStartActivity();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_apps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("권한 요청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void startAppStartActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startAppStartActivity();
            }
        }
    }
}