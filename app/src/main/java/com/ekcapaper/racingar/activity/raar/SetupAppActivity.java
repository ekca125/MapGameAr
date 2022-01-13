package com.ekcapaper.racingar.activity.raar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ekcapaper.racingar.R;

public class SetupAppActivity extends AppCompatActivity {

    Button button_request_permission;
    TextView textView_setup_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_app);

        button_request_permission = findViewById(R.id.button_request_permission);
        textView_setup_app = findViewById(R.id.textView_setup_app);


    }
}