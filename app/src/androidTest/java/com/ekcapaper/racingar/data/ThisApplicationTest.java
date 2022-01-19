package com.ekcapaper.racingar.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.util.Log;

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
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.NotificationList;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class ThisApplicationTest {
    public static ThisApplication thisApplication;
    public static SocketListener socketListenerEmpty;

    @BeforeClass
    public static void init() throws Exception {
        thisApplication = (ThisApplication) ApplicationProvider.getApplicationContext();
        socketListenerEmpty = new SocketListener() {
            @Override
            public void onDisconnect(Throwable t) {

            }

            @Override
            public void onError(Error error) {

            }

            @Override
            public void onChannelMessage(ChannelMessage message) {

            }

            @Override
            public void onChannelPresence(ChannelPresenceEvent presence) {

            }

            @Override
            public void onMatchmakerMatched(MatchmakerMatched matched) {

            }

            @Override
            public void onMatchData(MatchData matchData) {

            }

            @Override
            public void onMatchPresence(MatchPresenceEvent matchPresence) {

            }

            @Override
            public void onNotifications(NotificationList notifications) {

            }

            @Override
            public void onStatusPresence(StatusPresenceEvent presence) {

            }

            @Override
            public void onStreamPresence(StreamPresenceEvent presence) {

            }

            @Override
            public void onStreamData(StreamData data) {

            }
        };
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

    @Test
    public void groupSync() throws ExecutionException, InterruptedException {
        boolean status;
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);

        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";
        Group group = thisApplication.createGroupSync(groupName,groupDesc);
        assertNotNull(group);
        thisApplication.leaveGroupSync(group.getId());

        thisApplication.logout();
    }

    @Test
    public void groupGameRoomSync(){
        boolean status;
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);

        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";
        status =  thisApplication.createGameRoomGroupSync(groupName,groupDesc);
        assertTrue(status);
        String groupId = thisApplication.getGameRoomGroupId();
        Log.d("groupId",groupId);

        thisApplication.leaveGameRoomGroupSync();

        thisApplication.logout();

    }

    @Test
    public void matchSync() throws ExecutionException, InterruptedException {
        boolean status;
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);

        Match match = thisApplication.createMatchSync(socketListenerEmpty);
        assertNotNull(match);
        thisApplication.leaveMatchSync(match.getMatchId());

        thisApplication.logout();
    }


    @Test
    public void matchGameRoomSync() throws ExecutionException, InterruptedException {
        boolean status;
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);

        status = thisApplication.createGameRoomMatchSync(socketListenerEmpty);
        assertTrue(status);
        thisApplication.leaveGameRoomMatchSync();

        thisApplication.logout();
    }

    @Test
    public void gameRoomSync() throws ExecutionException, InterruptedException {
        boolean status;
        status = thisApplication.loginEmailSync(AccountStub.ID,AccountStub.PASSWORD);
        assertTrue(status);

        String groupName = RandomStringUtils.randomAlphabetic(10);
        String groupDesc = "";

        status = thisApplication.createGameRoom(groupName,groupDesc,socketListenerEmpty);
        assertTrue(status);

        thisApplication.leaveGameRoom();

        thisApplication.logout();
    }

}