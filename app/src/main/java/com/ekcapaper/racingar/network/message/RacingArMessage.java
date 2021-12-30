package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.RacingArOpCode;

public abstract class RacingArMessage {
    private final RacingArOpCode command;
    private final String payload;

    public RacingArMessage(RacingArOpCode command, String payload){
        this.command = command;
        this.payload = payload;
    }

    public RacingArOpCode getCommand() {
        return command;
    }

    public String getPayload() {
        return payload;
    }
}
