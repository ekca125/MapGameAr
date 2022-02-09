package com.ekcapaper.mapgamear.modelgame.play;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class GameTypeTextConverterTest {

    private void convertCheck(GameType originalGameType){
        String gameTypeText = GameTypeTextConverter.convertGameTypeToText(originalGameType);
        GameType gameType = GameTypeTextConverter.convertTextToGameType(gameTypeText);
        assertEquals(originalGameType, gameType);
    }

    @Test
    public void convertAllTest(){
        GameType[] gameTypes = GameType.values();
        for(GameType gameType:gameTypes){
            convertCheck(gameType);
        }
    }

    @Test
    public void convertGameTypeToText(){
        GameType gameType;
        String gameTypeToText;

        // flag
        gameType = GameType.GAME_TYPE_FLAG;
        gameTypeToText = GameTypeTextConverter.convertGameTypeToText(gameType);
        assertEquals(gameTypeToText,GameTypeTextConverter.GAME_TYPE_FLAG_TEXT);

        // tag
        gameType = GameType.GAME_TYPE_TAG;
        gameTypeToText = GameTypeTextConverter.convertGameTypeToText(gameType);
        assertEquals(gameTypeToText,GameTypeTextConverter.GAME_TYPE_TAG_TEXT);
    }

    @Test
    public void convertTextToGameType() {
        GameType gameType;
        String gameTypeToText;

        // flag
        gameTypeToText = GameTypeTextConverter.GAME_TYPE_FLAG_TEXT;
        gameType = GameTypeTextConverter.convertTextToGameType(gameTypeToText);
        assertEquals(gameType,GameType.GAME_TYPE_FLAG);

        // tag
        gameTypeToText = GameTypeTextConverter.GAME_TYPE_TAG_TEXT;
        gameType = GameTypeTextConverter.convertTextToGameType(gameTypeToText);
        assertEquals(gameType,GameType.GAME_TYPE_TAG);
    }
}