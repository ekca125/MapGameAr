package com.ekcapaper.racingar.operator.layer;

import com.ekcapaper.racingar.game.Player;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.UserPresence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class GameRoomLinker extends BaseRoomLinker {
    private final List<Player> playerList;

    public GameRoomLinker(@NonNull Client client,
                          @NonNull Session session,
                          @NonNull SocketClient socketClient) throws ExecutionException, InterruptedException {
        super(client, session, socketClient);
        playerList = new ArrayList<>();
        playerList.add(new Player(session.getUserId()));
    }

    public Optional<Player> getPlayer(String userId) {
        try {
            Player goalPlayer = playerList.stream()
                    .filter(player -> player.getUserId().equals(userId))
                    .collect(Collectors.toList()).get(0);
            return Optional.ofNullable(goalPlayer);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);

        // join 처리
        List<UserPresence> joinList = matchPresence.getJoins();
        if (joinList != null) {
            List<Player> joinPlayerList = joinList.stream()
                    .map((userPresence) -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            this.playerList.addAll(joinPlayerList);
        }

        // leave 처리
        List<UserPresence> leaveList = matchPresence.getLeaves();
        if (leaveList != null) {
            List<Player> leavePlayerList = leaveList.stream()
                    .map((userPresence -> new Player(userPresence.getUserId())))
                    .collect(Collectors.toList());
            this.playerList.removeAll(leavePlayerList);
        }
    }
}
