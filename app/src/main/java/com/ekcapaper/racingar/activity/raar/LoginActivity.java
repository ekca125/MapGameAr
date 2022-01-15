package com.ekcapaper.racingar.activity.raar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity implements ActivityInitializer{
    // 관제
    private ThisApplication thisApplication;
    // activity component
    private View parent_view;
    private TextInputEditText text_input_text_email;
    private TextInputEditText text_input_text_password;
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 액티비티 초기화
        initActivity();
        // 시스템 바의 설정
        Tools.setSystemBarColor(this);
        // stub
        text_input_text_email.setText(AccountStub.ID);
        text_input_text_password.setText(AccountStub.PASSWORD);
    }

    @Override
    public void initActivityField() {
        thisApplication = (ThisApplication) getApplicationContext();
    }

    @Override
    public void initActivityComponent() {
        parent_view = findViewById(android.R.id.content);
        text_input_text_email = findViewById(R.id.text_input_text_email);
        text_input_text_password = findViewById(R.id.text_input_text_password);
        button_login = findViewById(R.id.button_login);
    }

    @Override
    public void initActivityEventTask() {
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(text_input_text_email.getText()).toString();
                String password = Objects.requireNonNull(text_input_text_password.getText()).toString();

                CompletableFuture.runAsync(() -> {
                    thisApplication.login(email, password);
                }, thisApplication.getExecutorService())
                        .thenRun(() -> {
                            thisApplication.getSessionOptional().ifPresent(session -> {
                                Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                                startActivity(intent);
                            });
                        });
            }
        });
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Sign Up", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}