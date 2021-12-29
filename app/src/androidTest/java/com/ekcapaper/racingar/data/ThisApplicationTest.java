package com.ekcapaper.racingar.data;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;

import com.ekcapaper.racingar.AccountStub;

import org.junit.Test;

import java.util.function.Supplier;

public class ThisApplicationTest {

    @Test
    public void onCreate() {
    }

    @Test
    public void login() throws Exception {
        ThisApplication thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
        thisApplication.login(AccountStub.ID, AccountStub.PASSWORD);
        thisApplication.getSession().orElseThrow(() -> new Exception("Login Error"));
    }
}