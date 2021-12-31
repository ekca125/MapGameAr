package com.ekcapaper.racingar.operator;

import com.ekcapaper.racingar.retrofit.AddressMapClient;
import com.ekcapaper.racingar.retrofit.AddressMapService;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;

public class RoomOperatorFactory {
    public static RoomOperator createRoomOperator(String type, Session session, SocketClient socketClient, Match match){
        if(type.equals("flagGame")){
            AddressMapService addressMapService = AddressMapClient.getMapAddressService();
            addressMapService.


        }
        else{
            return null;
        }
    }
}
