package com.ekcapaper.racingar.activity.raar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.activity.raar.stub.AccountStub;
import com.ekcapaper.racingar.data.ThisApplication;
import com.ekcapaper.racingar.utils.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.heroiclabs.nakama.Session;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LoginActivity extends AppCompatActivity {
    private ThisApplication thisApplication;
    private View parent_view;

    private TextInputEditText text_input_text_email;
    private TextInputEditText text_input_text_password;
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent_view = findViewById(android.R.id.content);
        thisApplication = (ThisApplication) getApplicationContext();

        text_input_text_email = findViewById(R.id.text_input_text_email);
        text_input_text_password = findViewById(R.id.text_input_text_password);
        button_login = findViewById(R.id.button_login);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(text_input_text_email.getText()).toString();
                String password = Objects.requireNonNull(text_input_text_password.getText()).toString();

                CompletableFuture.runAsync(()->{
                    thisApplication.login(email,password);
                }, thisApplication.getExecutorService())
                .thenRun(()->{
                    thisApplication.getSessionOptional().ifPresent(session -> {
                        Intent intent = new Intent(LoginActivity.this,LobbyActivity.class);
                        startActivity(intent);
                    });
                });
            }
        });

        ((View) findViewById(R.id.sign_up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Sign Up", Snackbar.LENGTH_SHORT).show();
            }
        });

        Tools.setSystemBarColor(this);

        // stub
        text_input_text_email.setText(AccountStub.ID);
        text_input_text_password.setText(AccountStub.PASSWORD);
    }
}