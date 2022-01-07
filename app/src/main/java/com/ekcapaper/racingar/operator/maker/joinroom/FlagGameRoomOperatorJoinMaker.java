package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.operator.maker.dto.GameFlagListDto;
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

public class FlagGameRoomOperatorJoinMaker extends TimeLimitGameRoomOperatorJoinMaker implements FlagGameRoomOperatorMaker {
    private MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session, String matchId, Duration timeLimit) {
        super(client, session, matchId, timeLimit);
        mapRange = null;
        gameFlagList = null;
    }

    boolean readPrepareData() {
        try {
            // util
            Gson gson = new Gson();
            // storage 1 (MapRange)
            StorageObjectId storageObjectIdMapRange = new StorageObjectId(ServerRoomSaveDataNameSpace.getCollectionName(matchId));
            storageObjectIdMapRange.setKey(ServerRoomSaveDataNameSpace.getRoomPrepareKeyMapRangeName());
            storageObjectIdMapRange.setUserId(session.getUserId());

            StorageObjects storageObjectsMapRange = client.readStorageObjects(session, storageObjectIdMapRange).get();
            StorageObject storageObjectMapRange = storageObjectsMapRange.getObjects(0);
            MapRange mapRange = gson.fromJson(storageObjectMapRange.getValue(), MapRange.class);

            // storage 2 (GameFlagListDto)
            StorageObjectId storageObjectIdGameFlagListDto = new StorageObjectId(ServerRoomSaveDataNameSpace.getCollectionName(matchId));
            storageObjectIdMapRange.setKey(ServerRoomSaveDataNameSpace.getRoomPrepareKeyGameFlagListName());
            storageObjectIdMapRange.setUserId(session.getUserId());

            StorageObjects storageObjectsGameFlagListDto = client.readStorageObjects(session, storageObjectIdMapRange).get();
            StorageObject storageObjectGameFlagListDto = storageObjectsGameFlagListDto.getObjects(0);
            GameFlagListDto gameFlagListDto = gson.fromJson(storageObjectGameFlagListDto.getValue(), GameFlagListDto.class);
            List<GameFlag> gameFlagList = gameFlagListDto.getGameFlagList();

            this.mapRange = mapRange;
            this.gameFlagList = gameFlagList;
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        if (this.mapRange == null || this.gameFlagList == null) {
            throw new IllegalStateException();
        }
        FlagGameRoomOperator flagGameRoomOperator = new FlagGameRoomOperator(client, session, timeLimit, gameFlagList);

        return null;
    }
}
