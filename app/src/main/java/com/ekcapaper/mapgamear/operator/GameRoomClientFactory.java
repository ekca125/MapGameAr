package com.ekcapaper.mapgamear.operator;

import com.ekcapaper.mapgamear.nakama.NakamaNetworkManager;

public class GameRoomClientFactory {
    static private GameRoomClient createGameRoomClient(String clientTypeName, NakamaNetworkManager nakamaNetworkManager){
        GameRoomClient gameRoomClient = null;
        if(clientTypeName.equals(GameRoomClient.class.getName())){
            gameRoomClient = new GameRoomClient(nakamaNetworkManager);
        }
        else if(clientTypeName.equals(FlagGameRoomClient.class.getName())){
            gameRoomClient = new FlagGameRoomClient(nakamaNetworkManager);
        }
        else if(clientTypeName.equals(TagGameRoomClient.class.getName())){
            gameRoomClient = new TagGameRoomClient(nakamaNetworkManager);
        }
        return gameRoomClient;
    }


    static public GameRoomClient createGameRoomClientNewMatch(String clientTypeName, NakamaNetworkManager nakamaNetworkManager, String label){
        GameRoomClient gameRoomClient = createGameRoomClient(clientTypeName,nakamaNetworkManager);
        if(gameRoomClient == null){
            return null;
        }

        boolean result = gameRoomClient.createMatch(label);
        if(result){
            return gameRoomClient;
        }
        else{
            return null;
        }
    }

    static public GameRoomClient createGameRoomClientJoinMatch(String clientTypeName, NakamaNetworkManager nakamaNetworkManager, String matchId){
        GameRoomClient gameRoomClient = createGameRoomClient(clientTypeName,nakamaNetworkManager);
        if(gameRoomClient == null){
            return null;
        }
        boolean result = gameRoomClient.joinMatch(matchId);
        if(result){
            return gameRoomClient;
        }
        else{
            return null;
        }
    }

}
