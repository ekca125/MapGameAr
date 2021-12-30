package com.ekcapaper.racingar.network;

public class Message {
    OpCode opCode;
    String payload;

    public Message(OpCode opCode, String payload) {
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
