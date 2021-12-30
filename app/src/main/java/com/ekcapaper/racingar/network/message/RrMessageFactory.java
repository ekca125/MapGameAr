package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.RrNetworkCode;
import com.google.gson.Gson;

public class RrMessageFactory {
    static private Gson gson;
    static {
        gson = new Gson();
    }

    public static RrMessage createMessage(RrNetworkCode command, String payload){
        

        NetworkCode
        switch (command){
            case OP_CHAT:
                return new RacingArTestMessage(payload);
            default:
                return null;
        }
    }

    public static RacingArMessage parseMessage(String message){
        RacingArMessage racingArMessage = gson.fromJson(message,RacingArMessage.class);
        return createMessage(racingArMessage.getCommand(), racingArMessage.getPayload());
    }
}
