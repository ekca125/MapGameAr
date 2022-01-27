package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity{
    // manager
    private ThisApplication thisApplication;
    private NakamaNetworkManager nakamaNetworkManager;
    // activity
    private View parent_view;
    private TextInputEditText text_input_text_email;
    private TextInputEditText text_input_text_password;
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
/*
        // field
        this.thisApplication = (ThisApplication) getApplicationContext();
        this.nakamaNetworkManager = this.thisApplication.getNakamaNetworkManager();

        // activity
        this.parent_view = findViewById(android.R.id.content);
        this.text_input_text_email = findViewById(R.id.text_input_text_email);
        this.text_input_text_password = findViewById(R.id.text_input_text_password);
        this.button_login = findViewById(R.id.button_login);

        // activity setting
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(text_input_text_email.getText()).toString();
                String password = Objects.requireNonNull(text_input_text_password.getText()).toString();

                boolean result = thisApplication.getNakamaNetworkManager().loginEmailSync(email, password);
                if (result) {
                    Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Tools.setSystemBarColor(this);



        // stub
        text_input_text_email.setText(AccountStub.ID);
        text_input_text_password.setText(AccountStub.PASSWORD);
         */
    }

}