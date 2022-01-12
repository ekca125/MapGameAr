package com.ekcapaper.racingar.network;

import static org.junit.Assert.*;

import org.junit.Test;

public class MovePlayerGameMessageTest {

    @Test
    public void getPayload() {
        String userId = "a1234";
        double latitude = 12;
        double longitude = 23;

        GameMessageMovePlayer message = GameMessageMovePlayer
                .builder()
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        String payload = message.getPayload();

        assertFalse(payload.isEmpty());
        assertTrue(payload.contains("userId"));
        assertTrue(payload.contains("latitude"));
        assertTrue(payload.contains("longitude"));
    }
}