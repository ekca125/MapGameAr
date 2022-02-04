package com.ekcapaper.mapgamear.operator;

import android.location.Location;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TagGameRoomClient extends GameRoomClient{
    class Tagger{
        String taggerUserId;

        public Tagger(String taggerUserId) {
            this.taggerUserId = taggerUserId;
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
        // 술래 정하기
        List<Player> matchPlayers = matchUserPresenceList.stream()
                .map(userPresence -> new Player(userPresence.getUserId()))
                .collect(Collectors.toList());
        Collections.shuffle(matchPlayers);
        Player tagPlayer = matchPlayers.get(0);
        // 게임 시작
        GameMessageTagGameStart gameMessageStart = new GameMessageTagGameStart(tagPlayer.getUserId());
        sendMatchData(gameMessageStart);
    }

    @Override
    public void onGameStart(GameMessageStart gameMessageStart) {
        if(gameMessageStart instanceof GameMessageTagGameStart){
            GameMessageTagGameStart gameMessageTagGameStart = (GameMessageTagGameStart) gameMessageStart;
            if (currentGameStatus == GameStatus.GAME_READY) {
                // ready 상태에서만 메시지를 처리한다.
                tagger = new Tagger(gameMessageTagGameStart.getTaggerUserId());
                goGameStatus(GameStatus.GAME_RUNNING);
                afterGameStartMessage.run();
            }
        }
    }

    @Override
    public void onMovePlayer(GameMessageMovePlayer gameMessageMovePlayer) {
        super.onMovePlayer(gameMessageMovePlayer);

    }

}
