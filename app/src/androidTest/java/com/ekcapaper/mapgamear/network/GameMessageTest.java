package com.ekcapaper.mapgamear.network;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameMessageTest {
    @Test
    public void testGameMessageStart(){
        GameMessageStart gameMessageStart = new GameMessageStart();
        assertEquals(gameMessageStart.getOpCode(),GameMessageOpCode.GAME_START);
    }

    @Test
    public void testGameMessageFlagGameStart(){
        GameMessageStart gameMessageStart = new GameMessageFlagGameStart(null);
        assertEquals(gameMessageStart.getOpCode(),GameMessageOpCode.GAME_START);
    }

    @Test
    public void testGameMessageTagGameStart(){
        GameMessageStart gameMessageStart = new GameMessageTagGameStart("stub");
        assertEquals(gameMessageStart.getOpCode(),GameMessageOpCode.GAME_START);
    }

    @Test
    public void testGameMessageMovePlayer(){
        GameMessageMovePlayer gameMessageMovePlayer = new GameMessageMovePlayer(null,0,0);
        assertEquals(gameMessageMovePlayer.getOpCode(),GameMessageOpCode.MOVE_PLAYER);
    }

    @Test
    public void testGameMessageEnd(){
        GameMessageEnd gameMessageEnd = new GameMessageEnd();
        assertEquals(gameMessageEnd.getOpCode(),GameMessageOpCode.GAME_END);
    }
}