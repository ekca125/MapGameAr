package com.ekcapaper.racingar.operator;

import android.location.Location;

import com.ekcapaper.racingar.modelgame.GameRoomLabel;
import com.ekcapaper.racingar.modelgame.address.MapRange;
import com.ekcapaper.racingar.modelgame.play.GameStatus;
import com.ekcapaper.racingar.modelgame.play.Player;
import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.ekcapaper.racingar.modelgame.play.GameFlag;
import com.ekcapaper.racingar.network.GameMessageFlagGameStart;
import com.ekcapaper.racingar.network.GameMessageMovePlayer;
import com.ekcapaper.racingar.network.GameMessageStart;
import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;

public class FlagGameRoomClient extends GameRoomClient {
    private List<GameFlag> gameFlagList;

    public FlagGameRoomClient(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);
        gamePlayerList.stream()
                .filter(player -> gameMessageMovePlayer.getUserId().equals(player.getUserId()))
                .forEach(player -> {
                    player.getLocation().ifPresent((Location location) -> {
                        gameFlagList.stream().forEach((GameFlag gameFlag) -> {
                            gameFlag.reflectPlayerLocation(location, player.getUserId());
                        });
                    });
                });
    }

    @Override
    public void declareGameStart() {
        if (currentGameStatus != GameStatus.GAME_READY) {
            // ready 상태에서만 시작을 선언할 수 있다.
            throw new IllegalStateException();
        }
        //
        Gson gson = new Gson();
        String label = getMatch().getLabel();
        GameRoomLabel gameRoomLabel = gson.fromJson(label,GameRoomLabel.class);
        // 깃발 가져오기
        try {
            List<AddressDto> addressDtoList = AddressMapClient.getMapAddressService()
                    .drawMapRangeRandom10((MapRange) gameRoomLabel).execute().body();
            List<GameFlag> gameFlagList = addressDtoList.stream().map(addressDto -> {
                Location location = new Location("");
                location.setLatitude(addressDto.getLatitude());
                location.setLongitude(addressDto.getLongitude());
                return new GameFlag(location);
            }).collect(Collectors.toList());
            // 메시지 전송
            GameMessageFlagGameStart gameMessageStart = new GameMessageFlagGameStart(gameFlagList);
            sendMatchData(gameMessageStart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameStart(GameMessageStart gameMessageStart) {
        if(gameMessageStart instanceof GameMessageFlagGameStart){
            GameMessageFlagGameStart gameMessageFlagGameStart = (GameMessageFlagGameStart) gameMessageStart;
            if (currentGameStatus == GameStatus.GAME_READY) {
                // ready 상태에서만 메시지를 처리한다.
                // 깃발 설정
                gameFlagList = gameMessageFlagGameStart.getGameFlagList();
                // 플레이어 설정
                List<Player> matchPlayers = matchUserPresenceList.stream()
                        .map(userPresence -> new Player(userPresence.getUserId()))
                        .collect(Collectors.toList());
                gamePlayerList.addAll(matchPlayers);
                goGameStatus(GameStatus.GAME_RUNNING);
                afterGameStartMessage.run();
            }
        }
    }

    public int getPoint(String userId) {
        return (int) gameFlagList.stream()
                .filter(gameFlag -> gameFlag.getUserId().equals(userId))
                .count();
    }

    public List<GameFlag> getUnownedFlagList() {
        return gameFlagList.stream()
                .filter(gameFlag -> !gameFlag.isOwned())
                .collect(Collectors.toList());
    }

}
