package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.RrNetworkCode;

public class RrMessage {
    private final RrNetworkCode command;
    private final String payload;

    public RrMessage(RrNetworkCode command, String payload) {
        this.command = command;
        this.payload = payload;
    }

    public RrNetworkCode getCommand() {
        return command;
    }

    public String getPayload() {
        return payload;
    }
}
