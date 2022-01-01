package com.ekcapaper.racingar.operator;

import android.graphics.Path;
import android.location.Location;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Setter;

public abstract class RoomOperator extends BaseRoomOperator {
    private final List<Player> playerList;
    public RoomOperator(Client client, Session session) throws ExecutionException, InterruptedException {
        super(client, session);
        playerList = new ArrayList<>();
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        List<Player> joinPlayerList = joinList.stream()
                .map((userPresence) -> new Player(userPresence.getUserId()))
                .collect(Collectors.toList());
        this.playerList.addAll(joinPlayerList);

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        List<Player> leavePlayerList = leaveList.stream()
                .map((userPresence -> new Player(userPresence.getUserId())))
                .collect(Collectors.toList());
        this.playerList.removeAll(leavePlayerList);

        // 새로 들어온 사람이 위치를 갱신할 수 있도록 이동메시지를 보낸다.
        getCurrentPlayer().ifPresent((player -> {
            player.getLocation().ifPresent(this::moveCurrentPlayer);
        }));
    }

    @Override
    protected void onMovePlayer(MovePlayerMessage movePlayerMessage){
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(movePlayerMessage.getLatitude());
            location.setLongitude(movePlayerMessage.getLongitude());
            player.updateLocation(location);
        }));
    }

    public Optional<Player> getPlayer(String userId){
        try {
            return Optional.ofNullable(playerList
                    .stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .collect(Collectors.toList()).get(0));
        }
        catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }

    public Optional<Player> getCurrentPlayer() {
        return getPlayer(getCurrentUserId());
    }

    // 주기적으로 확인하는 형태를 취함
    private ScheduledExecutorService scheduledExecutorServiceEndCheck;

    protected abstract boolean isEnd();
    protected abstract boolean isVictory();
    protected abstract boolean isDefeat();

    protected void endCheck(){
        if(isEnd()){
            finishSequence();
            if(isVictory()){
                victorySequence();
            }
            else if(isDefeat()){
                defeatSequence();
            }
        }
    }

    @Override
    public void startSequence() {
        super.startSequence();
        scheduledExecutorServiceEndCheck = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorServiceEndCheck.scheduleWithFixedDelay(this::endCheck,1,1, TimeUnit.SECONDS);
    }


}
