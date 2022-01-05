package com.ekcapaper.racingar.network;

import com.google.gson.Gson;

public abstract class Message {
    private final OpCode opCode;

    public Message(OpCode opCode) {
        this.opCode = opCode;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public String getPayload() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
