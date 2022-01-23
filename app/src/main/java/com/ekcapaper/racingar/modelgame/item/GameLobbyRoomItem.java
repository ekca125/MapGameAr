package com.ekcapaper.racingar.modelgame.item;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.modelgame.play.GameType;

import lombok.Builder;

@Builder
public class GameLobbyRoomItem {
    // info
    public final String groupId;
    public final String matchId;
    public GameLobbyRoomItem(String groupId, String matchId) {
        this.groupId = groupId;
        this.matchId = matchId;
    }
}
