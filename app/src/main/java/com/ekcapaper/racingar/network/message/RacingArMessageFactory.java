package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.model.Message;
import com.ekcapaper.racingar.network.RacingArOpCode;

public class RacingArMessageFactory {
    public static Message createTestMessage(RacingArOpCode command, String testMessage){
        RacingArTestMessage racingArTestMessage = new RacingArTestMessage(command,testMessage);
        return racingArTestMessage;
    }

}
