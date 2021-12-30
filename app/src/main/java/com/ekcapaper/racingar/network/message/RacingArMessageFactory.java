package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.model.Message;
import com.ekcapaper.racingar.network.RacingArOpCode;
import com.google.gson.Gson;

public class RacingArMessageFactory {
    static private Gson gson;
    static{
        gson = new Gson();
    }

    public static RacingArMessage createMessage(RacingArOpCode command, String payload){
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
