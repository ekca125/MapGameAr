package com.ekcapaper.racingar.network;

public abstract class Message {
    OpCode opCode;

    public Message(OpCode opCode) {
        this.opCode = opCode;
    }

    public Message(OpCode opCode, String payload) {
        this.opCode = opCode;
    }

    public OpCode getOpCode() {
        return opCode;
    }
    public abstract String getPayload();
}
