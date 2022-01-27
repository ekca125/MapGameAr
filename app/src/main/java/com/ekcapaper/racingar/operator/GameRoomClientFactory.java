package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.nakama.NakamaNetworkManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

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


    static public GameRoomClient createGameRoomClientNewMatch(String clientTypeName, NakamaNetworkManager nakamaNetworkManager, String label){
        Gson gson = new Gson();
        //
        GameRoomClient gameRoomClient = createGameRoomClient(clientTypeName,nakamaNetworkManager);
        if(gameRoomClient == null){
            return null;
        }

        Map<String,String> payload = new HashMap<>();
        payload.put("label",label);

        boolean result = gameRoomClient.createMatch(gson.toJson(payload));
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
