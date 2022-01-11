package com.ekcapaper.racingar.data;

import androidx.test.core.app.ApplicationProvider;

import com.ekcapaper.racingar.activity.raar.stub.AccountStub;

import org.junit.Test;

public class ThisApplicationTest {

    @Test
    public void onCreate() {
    }

    @Test
    public void login() throws Exception {
        ThisApplication thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
        thisApplication.login(AccountStub.ID, AccountStub.PASSWORD);
        thisApplication.getSessionOptional().orElseThrow(() -> new Exception("Login Error"));
    }
}