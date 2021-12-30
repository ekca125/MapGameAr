package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.RacingArOpCode;

public class RacingArTestMessage extends RacingArMessage{
    public RacingArTestMessage(RacingArOpCode command, String payload) {
        super(command, payload);
    }
}
