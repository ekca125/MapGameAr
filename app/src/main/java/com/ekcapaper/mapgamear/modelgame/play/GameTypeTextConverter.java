package com.ekcapaper.mapgamear.modelgame.play;

public class GameTypeTextConverter {
    static public final String GAME_TYPE_FLAG_TEXT = "깃발게임";
    static public final String GAME_TYPE_TAG_TEXT = "술래잡기게임";
    static public final String DEFAULT_GAME_TYPE_TEXT = "정의되지 않은 게임타입입니다.";

    static public String convertGameTypeToText(GameType gameType){
        if (gameType == GameType.GAME_TYPE_FLAG) {
            return GameTypeTextConverter.GAME_TYPE_FLAG_TEXT;
        }
        if(gameType == GameType.GAME_TYPE_TAG){
            return GameTypeTextConverter.GAME_TYPE_TAG_TEXT;
        }
        else {
            return GameTypeTextConverter.DEFAULT_GAME_TYPE_TEXT;
        }
    }

    static public GameType convertTextToGameType(String gameTypeText){
        if(gameTypeText.equals(GAME_TYPE_FLAG_TEXT)){
            return GameType.GAME_TYPE_FLAG;
        }
        else if(gameTypeText.equals(GAME_TYPE_TAG_TEXT)){
            return GameType.GAME_TYPE_TAG;
        }
        else{
            // 잘못된 텍스트값인 경우
            throw new IllegalArgumentException();
        }
    }
}
