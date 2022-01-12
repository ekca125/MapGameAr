package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.modelgame.SaveDataNameDefine;
import com.ekcapaper.racingar.modelgame.RoomInfoTimeLimit;
import com.ekcapaper.racingar.operator.maker.make.TimeLimitGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.readwrite.RoomInfoReader;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class TimeLimitGameRoomOperatorJoinMaker extends GameRoomOperatorJoinMaker implements TimeLimitGameRoomOperatorMaker, RoomInfoReader {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorJoinMaker(Client client, Session session, String matchId) {
        super(client, session, matchId);
    }

    @Override
    public TimeLimitGameRoomOperator makeTimeLimitGameRoomOperator() {
        boolean result = readRoomInfo();
        if (!result) {
            return null;
        }
        TimeLimitGameRoomOperator timeLimitGameRoomOperator = new TimeLimitGameRoomOperator(client, session, timeLimit);
        timeLimitGameRoomOperator.joinMatch(matchId);
        return timeLimitGameRoomOperator;
    }

    @Override
    public boolean readRoomInfo() {
        try {
            // util
            Gson gson = new Gson();
            // storage 1 (RoomInfo)
            StorageObjectId storageObjectId = new StorageObjectId(SaveDataNameDefine.getCollectionName(matchId));
            storageObjectId.setKey(SaveDataNameDefine.getDataRoomInfoKey());
            storageObjectId.setUserId(session.getUserId());

            StorageObjects storageObjects = client.readStorageObjects(session, storageObjectId).get();
            StorageObject storageObject = storageObjects.getObjects(0);
            RoomInfoTimeLimit roomInfoTimeLimit = gson.fromJson(storageObject.getValue(), RoomInfoTimeLimit.class);
            timeLimit = Duration.ofSeconds(roomInfoTimeLimit.getTimeLimitSeconds());
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
