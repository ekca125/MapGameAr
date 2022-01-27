package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;

public class GameRoomClientFactory {
    static private GameRoomClient createGameRoomClient(String clientTypeName, NakamaNetworkManager nakamaNetworkManager){
        GameRoomClient gameRoomClient = null;
        if(clientTypeName.equals(GameRoomClient.class.getName())){
            gameRoomClient = new GameRoomClient(nakamaNetworkManager);
        }
        else if(clientTypeName.equals(FlagGameRoomClient.class.getName())){
            gameRoomClient = new FlagGameRoomClient(nakamaNetworkManager);
        }
        return gameRoomClient;
    }


    static public GameRoomClient createGameRoomClientNewMatch(String clientTypeName, NakamaNetworkManager nakamaNetworkManager, String payload){
        GameRoomClient gameRoomClient = createGameRoomClient(clientTypeName,nakamaNetworkManager);
        if(gameRoomClient == null){
            return null;
        }
        boolean result = gameRoomClient.createMatch(payload);
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
