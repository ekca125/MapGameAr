package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.game.Player;
import com.ekcapaper.racingar.network.MovePlayerMessage;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.UserPresence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RoomClient extends RoomLinker{
    private final List<Player> playerList;
    private final String currentUserId;

    public RoomClient(Client client, Session session) throws ExecutionException, InterruptedException {
        super(client, session);
        playerList = new ArrayList<>();
        currentUserId = session.getUserId();
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

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        // join 처리
        Optional<List<UserPresence>> joinListOptional = Optional.ofNullable(matchPresence.getJoins());
        joinListOptional.ifPresent((joinList)->{
            List<Player> joinPlayerList = joinList.stream()
                    .map((userPresence) -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            this.playerList.addAll(joinPlayerList);
        });

        // leave 처리
        Optional<List<UserPresence>> leaveListOptional = Optional.ofNullable(matchPresence.getLeaves());
        leaveListOptional.ifPresent((leaveList)->{
            List<Player> leavePlayerList = leaveList.stream()
                    .map((userPresence -> new Player(userPresence.getUserId())))
                    .collect(Collectors.toList());
            this.playerList.removeAll(leavePlayerList);
        });

        // 새로 들어온 사람이 위치를 갱신할 수 있도록 이동메시지를 보낸다.
        getPlayer(currentUserId).ifPresent((player -> {
            player.getLocation().ifPresent(location -> {
                MovePlayerMessage movePlayerMessage = MovePlayerMessage.builder()
                        .userId(player.getUserId())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();
                sendMatchData(movePlayerMessage);
            });
        }));
    }

    @Override
    protected void onMovePlayer(MovePlayerMessage movePlayerMessage) {
        Optional<Player> optionalPlayer = getPlayer(movePlayerMessage.getUserId());
        optionalPlayer.ifPresent((player -> {
            Location location = new Location("");
            location.setLatitude(movePlayerMessage.getLatitude());
            location.setLongitude(movePlayerMessage.getLongitude());
            player.updateLocation(location);
        }));
    }
}
