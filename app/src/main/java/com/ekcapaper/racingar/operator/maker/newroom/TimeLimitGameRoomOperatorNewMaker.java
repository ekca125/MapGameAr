package com.ekcapaper.racingar.operator.maker.newroom;

import android.util.Log;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.PrepareDataGetter;
import com.ekcapaper.racingar.operator.maker.PrepareDataWriter;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.operator.maker.TimeLimitGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.dto.GameFlagListDto;
import com.ekcapaper.racingar.operator.maker.dto.PrepareDataTimeLimit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TimeLimitGameRoomOperatorNewMaker extends GameRoomOperatorNewMaker implements TimeLimitGameRoomOperatorMaker, PrepareDataGetter, PrepareDataWriter {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
    }

    @Override
    public JsonObject getPrepareData() {
        // Util
        Gson gson = new Gson();
        // data
        PrepareDataTimeLimit prepareDataTimeLimit = new PrepareDataTimeLimit(timeLimit.getSeconds());
        // convert
        String json = gson.toJson(prepareDataTimeLimit);
        return gson.fromJson(json,JsonObject.class);
    }

    @Override
    public boolean writePrepareData(String matchId) {
        // collection
        String collectionName = ServerRoomSaveDataNameSpace.getCollectionName(matchId);
        // data 1
        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                ServerRoomSaveDataNameSpace.getRoomPrepareKey(),
                getPrepareData().toString(),
                PermissionRead.PUBLIC_READ,
                PermissionWrite.OWNER_WRITE
        );
        try {
            client.writeStorageObjects(session, saveGameObject).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("test", e.toString());
            return false;
        }
        return true;
    }

    @Override
    public TimeLimitGameRoomOperator makeTimeLimitGameRoomOperator() {
        TimeLimitGameRoomOperator timeLimitGameRoomOperator = new TimeLimitGameRoomOperator(client, session, timeLimit);
        timeLimitGameRoomOperator.createMatch();
        return timeLimitGameRoomOperator;
    }



}
