package com.ekcapaper.racingar.activity.raar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.utils.Tools;

public class SetupAppActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 1;
    private final int ACTIVITY_REQUEST_CODE = 2;

    TextView textView_setup_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_app);

        textView_setup_app = findViewById(R.id.textView_setup_app);
        permissionCheckAndAction();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_apps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("권한 요청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void startAppStartActivity(){



        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent,ACTIVITY_REQUEST_CODE);
    }

    protected void permissionCheckAndAction(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startAppStartActivity();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}