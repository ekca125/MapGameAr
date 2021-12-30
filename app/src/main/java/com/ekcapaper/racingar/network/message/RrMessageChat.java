package com.ekcapaper.racingar.network.message;

import com.ekcapaper.racingar.network.RrNetworkCode;

public class RrMessageChat extends RrMessage{
    public RrMessageChat(RrNetworkCode command, String payload) {
        super(command, payload);
    }
}
