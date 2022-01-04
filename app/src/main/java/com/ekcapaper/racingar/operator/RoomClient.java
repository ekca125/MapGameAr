package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.game.Player;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.UserPresence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.NonNull;

public class RoomClient extends RoomLinker {
    private List<Player> playerList;

    public RoomClient(@NonNull Client client,
                      @NonNull Session session,
                      @NonNull SocketClient socketClient,
                      @NonNull Match match,
                      @NonNull Channel chatChannel) throws ExecutionException, InterruptedException {
        super(client, session, socketClient, match, chatChannel);
        playerList = new ArrayList<>();
        playerList.add(new Player(session.getUserId()));
    }

    public Optional<Player> getPlayer(String userId) {
        try{
            Player goalPlayer = playerList.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .collect(Collectors.toList()).get(0);
            return Optional.ofNullable(goalPlayer);
        }
        catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        Optional<List<UserPresence>> joinListOptional = Optional.ofNullable(matchPresence.getJoins());
        joinListOptional.ifPresent((joinList) -> {
            List<Player> joinPlayerList = joinList.stream()
                    .map((userPresence) -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            this.playerList.addAll(joinPlayerList);
        });

        // leave 처리
        Optional<List<UserPresence>> leaveListOptional = Optional.ofNullable(matchPresence.getLeaves());
        leaveListOptional.ifPresent((leaveList) -> {
            List<Player> leavePlayerList = leaveList.stream()
                    .map((userPresence -> new Player(userPresence.getUserId())))
                    .collect(Collectors.toList());
            this.playerList.removeAll(leavePlayerList);
        });
    }
}
