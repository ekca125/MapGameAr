package com.ekcapaper.racingar.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameType;
import com.ekcapaper.racingar.operator.layer.GameRoomPlayClient;
import com.ekcapaper.racingar.stub.AccountStub;
import com.ekcapaper.racingar.stub.LocationStub;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ThisApplicationTest {
    public static ThisApplication thisApplication;

    @BeforeClass
    public static void init() throws Exception {
        thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
    }

    @Test
    public void loginEmailSync() {
        boolean status;
        status = thisApplication.isLogin();
        assertFalse(status);
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);
        status = thisApplication.isLogin();
        assertTrue(status);
        thisApplication.logout();
        status = thisApplication.isLogin();
        assertFalse(status);
    }
/*
    @Test
    public void groupSync(){
        String groupName = "test_group"+String.valueOf(ThreadLocalRandom.current().nextInt());
        String groupDesc = "";
        thisApplication.createGroupSync(groupName,groupDesc);
        thisApplication.leaveCurrentGroupSync();
    }
*/

}