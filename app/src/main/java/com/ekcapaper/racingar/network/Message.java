package com.ekcapaper.racingar.network;

public abstract class Message {
    private final OpCode opCode;

    public Message(OpCode opCode) {
        this.opCode = opCode;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public abstract String getPayload();
}
