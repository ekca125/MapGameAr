package com.ekcapaper.mapgamear.operator;

import android.location.Location;
import android.util.Log;

import com.ekcapaper.mapgamear.modelgame.GameRoomLabel;
import com.ekcapaper.mapgamear.modelgame.address.MapRange;
import com.ekcapaper.mapgamear.modelgame.play.GameFlag;
import com.ekcapaper.mapgamear.modelgame.play.GameStatus;
import com.ekcapaper.mapgamear.modelgame.play.Player;
import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;
import com.ekcapaper.mapgamear.network.GameMessageFlagGameStart;
import com.ekcapaper.mapgamear.network.GameMessageMovePlayer;
import com.ekcapaper.mapgamear.network.GameMessageStart;
import com.ekcapaper.mapgamear.network.GameMessageTagGameStart;
import com.ekcapaper.mapgamear.retrofit.AddressMapClient;
import com.ekcapaper.mapgamear.retrofit.dto.AddressDto;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TagGameRoomClient extends GameRoomClient{
    class Tagger{
        String taggerUserId;
        // setting
        double tagDistanceMeter;
        Duration tagFreeDuration;
        // data
        LocalDateTime tagTime;
        LocalDateTime tagFreeTime;
        // 100m 안으로 들어오면 술래가 되며 이때 잡힌 시점으로부터 1분이 지나야 술래가 변경된다.

        public Tagger(String taggerUserId) {
            // tagger
            this.taggerUserId = taggerUserId;
            // setting
            tagDistanceMeter = 100;
            tagFreeDuration = Duration.ofMinutes(1);
            // data
            tagTime = LocalDateTime.now();
            tagFreeTime = tagTime.plusMinutes(tagFreeDuration.toMinutes());
        }

        public void updateTagger(){
            LocalDateTime localDateTime = LocalDateTime.now();
            if(localDateTime.isAfter(tagFreeTime)){
                TagGameRoomClient.this.gamePlayerList.stream()
                        .filter(player -> !player.getUserId().equals(taggerUserId))
                        .limit(1)
                        .forEach(player -> taggerUserId = player.getUserId());
            }
        }
    }
    Tagger tagger;

    public TagGameRoomClient(NakamaNetworkManager nakamaNetworkManager) {
        super(nakamaNetworkManager);
    }

    @Override
    public void declareGameStart() {
        if (currentGameStatus != GameStatus.GAME_READY) {
            // ready 상태에서만 시작을 선언할 수 있다.
            throw new IllegalStateException();
        }
        // 술래 정하기 (시작하는 플레이어는 있으므로 1명의 플레이어는 무조건 존재한다.)
        try {
            List<Player> matchPlayers = matchUserPresenceList.stream()
                    .map(userPresence -> new Player(userPresence.getUserId()))
                    .collect(Collectors.toList());
            Collections.shuffle(matchPlayers);
            Player tagPlayer = matchPlayers.get(0);
            // 게임 시작
            GameMessageTagGameStart gameMessageStart = new GameMessageTagGameStart(tagPlayer.getUserId());
            sendMatchData(gameMessageStart);
        }
        catch (IndexOutOfBoundsException ignored){
        }
    }

    @Override
    public void onGameStart(GameMessageStart gameMessageStart) {
        if(gameMessageStart instanceof GameMessageTagGameStart){
            GameMessageTagGameStart gameMessageTagGameStart = (GameMessageTagGameStart) gameMessageStart;
            if (currentGameStatus == GameStatus.GAME_READY) {
                // ready 상태에서만 메시지를 처리한다.
                List<Player> matchPlayers = matchUserPresenceList.stream()
                        .map(userPresence -> new Player(userPresence.getUserId()))
                        .collect(Collectors.toList());
                gamePlayerList.addAll(matchPlayers);
                tagger = new Tagger(gameMessageTagGameStart.getTaggerUserId());
                goGameStatus(GameStatus.GAME_RUNNING);
                afterGameStartMessage.run();
            }
        }
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);
        tagger.updateTagger();
    }

    // 중간에 나간 경우에는 오류 가능성이 있다.
    public Player getCurrentTaggerPlayer(){
        return getGamePlayerList().stream()
                .filter(player -> tagger.taggerUserId.equals(player.getUserId()))
                .collect(Collectors.toList())
                .get(0);
    }
}
