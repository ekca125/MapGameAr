package com.ekcapaper.racingar.activity.dashboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.utils.Tools;

public class DashboardDana extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_dana);
        initToolbar();
    }

    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.blue_500);
    }
}
