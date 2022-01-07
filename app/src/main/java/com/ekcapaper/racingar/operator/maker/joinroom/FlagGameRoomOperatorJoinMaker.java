package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.SaveDataNameDefine;
import com.ekcapaper.racingar.operator.maker.data.RoomInfoReader;
import com.ekcapaper.racingar.operator.maker.data.RoomPrepareDataReader;
import com.ekcapaper.racingar.operator.maker.dto.PrepareDataFlagGameRoom;
import com.ekcapaper.racingar.operator.maker.dto.RoomInfoFlagGame;
import com.ekcapaper.racingar.operator.maker.make.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FlagGameRoomOperatorJoinMaker extends TimeLimitGameRoomOperatorJoinMaker implements FlagGameRoomOperatorMaker, RoomInfoReader, RoomPrepareDataReader {
    private MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session, String matchId) {
        super(client, session, matchId);
        mapRange = null;
        gameFlagList = null;
    }

    @Override
    public boolean readRoomInfo() {
        try {
            // util
            Gson gson = new Gson();
            //
            StorageObjectId storageObjectId = new StorageObjectId(SaveDataNameDefine.getCollectionName(matchId));
            storageObjectId.setKey(SaveDataNameDefine.getDataRoomInfoKey());
            storageObjectId.setUserId(session.getUserId());

            StorageObjects storageObjectsMapRange = client.readStorageObjects(session, storageObjectId).get();
            StorageObject storageObjectMapRange = storageObjectsMapRange.getObjects(0);
            RoomInfoFlagGame roomInfoFlagGame = gson.fromJson(storageObjectMapRange.getValue(), RoomInfoFlagGame.class);

            timeLimit = Duration.ofSeconds(roomInfoFlagGame.getTimeLimitSeconds());
            mapRange = roomInfoFlagGame.getMapRange();
        }
        catch (ExecutionException | InterruptedException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean readPrepareData() {
        try {
            // util
            Gson gson = new Gson();

            // storage 2 (GameFlagListDto)
            StorageObjectId storageObjectIdGameFlagListDto = new StorageObjectId(SaveDataNameDefine.getCollectionName(matchId));
            storageObjectIdGameFlagListDto.setKey(SaveDataNameDefine.getDataRoomPrepareKey());
            storageObjectIdGameFlagListDto.setUserId(session.getUserId());

            StorageObjects storageObjectsGameFlagListDto = client.readStorageObjects(session, storageObjectIdGameFlagListDto).get();
            StorageObject storageObjectGameFlagListDto = storageObjectsGameFlagListDto.getObjects(0);
            PrepareDataFlagGameRoom prepareDataFlagGameRoom = gson.fromJson(storageObjectGameFlagListDto.getValue(), PrepareDataFlagGameRoom.class);
            List<GameFlag> gameFlagList = prepareDataFlagGameRoom.getGameFlagList();

            this.gameFlagList = gameFlagList;
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        // 방 데이터 읽기
        boolean result = readRoomInfo() && readPrepareData();
        if (!result) {
            return null;
        }
        // 방 입장
        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, timeLimit, gameFlagList);
        flagGameRoomOperator.joinMatch(matchId);
        return flagGameRoomOperator;
    }
}
