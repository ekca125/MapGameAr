package com.ekcapaper.racingar.modelgame.item;

import lombok.Builder;

@Builder
public class GameLobbyRoomItem {
    // info
    public final String name;
    public final String groupId;
    public final String matchId;
    public GameLobbyRoomItem(String name, String groupId, String matchId) {
        this.name = name;
        this.groupId = groupId;
        this.matchId = matchId;
    }
}
