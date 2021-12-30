package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.OpCode;

public class RacingArMessage {
    OpCode opCode;
    String payload;

    public RacingArMessage(OpCode opCode, String payload) {
        this.opCode = opCode;
        this.payload = payload;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public String getPayload() {
        return payload;
    }
}
