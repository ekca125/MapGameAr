package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.function.Supplier;

public class ThisApplicationTest {

    @Test
    public void onCreate() {
    }

    @Test
    public void login() throws Exception {
        ThisApplication thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
        String email = "abcd@example.com";
        String password = "abcdefg";
        thisApplication.login(email, password);
        thisApplication.getSession().orElseThrow(() -> new Exception("Login Error"));
    }
}