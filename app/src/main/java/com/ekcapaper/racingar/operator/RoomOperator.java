package com.ekcapaper.racingar.operator;

import android.graphics.Path;
import android.location.Location;
import android.os.Looper;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.keystorage.KeyStorageNakama;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.ekcapaper.racingar.network.OpCode;
import com.google.gson.Gson;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;
import com.heroiclabs.nakama.api.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import lombok.Setter;

public abstract class RoomOperator extends RoomClient {
    private enum RoomStatus{
        READY,
        PROGRESS,
        END
    }
    RoomStatus roomStatus;
    final Timer roomEndChecker;
    final TimerTask roomEndCheckerTask;
    public RoomOperator(Client client, Session session) throws ExecutionException, InterruptedException {
        super(client, session);
        roomStatus = RoomStatus.READY;
        roomEndChecker = new Timer();
        roomEndCheckerTask = new TimerTask() {
            @Override
            public void run() {
                if(isEnd()){
                    endSequence();
                }
            }
        };
    }

    public void startGame(){
        if(roomStatus == RoomStatus.READY) {
            roomStatus = RoomStatus.PROGRESS;
            roomEndChecker.schedule(roomEndCheckerTask,0,1000);
        }
    }

    // end
    protected abstract boolean isEnd();
    protected final void endSequence(){
        if(isVictory()){
            victorySequence();
        }
        else if(isDefeat()){
            defeatSequence();
        }
        else{
            defaultSequence();
        }
    }

    // victory
    protected abstract boolean isVictory();
    protected abstract void victorySequence();

    // defeat
    protected abstract boolean isDefeat();
    protected abstract void defeatSequence();

    // default
    protected abstract void defaultSequence();
}
