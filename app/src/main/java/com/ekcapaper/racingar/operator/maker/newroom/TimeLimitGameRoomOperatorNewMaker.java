package com.ekcapaper.racingar.operator.maker.newroom;

import android.util.Log;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.operator.maker.TimeLimitGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.dto.GameFlagListDto;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TimeLimitGameRoomOperatorNewMaker extends GameRoomOperatorNewMaker implements TimeLimitGameRoomOperatorMaker {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
    }

    boolean writePrepareData(String matchId, Duration timeLimit) {
        // util
        Gson gson = new Gson();
        // collection
        String collectionName = ServerRoomSaveDataNameSpace.getCollectionName(matchId);

        // data 1
        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                ServerRoomSaveDataNameSpace.getRoomPrepareKeyTimeLimit(),
                gson.toJson(timeLimit.toString()),
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
