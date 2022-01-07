package com.ekcapaper.racingar.operator.maker.newroom;

import android.util.Log;

import com.ekcapaper.racingar.operator.layer.TimeLimitGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.SaveDataNameDefine;
import com.ekcapaper.racingar.operator.maker.data.RoomInfoTimeLimit;
import com.ekcapaper.racingar.operator.maker.make.TimeLimitGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.readwrite.RoomInfoWriter;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class TimeLimitGameRoomOperatorNewMaker extends GameRoomOperatorNewMaker implements TimeLimitGameRoomOperatorMaker, RoomInfoWriter {
    Duration timeLimit;

    public TimeLimitGameRoomOperatorNewMaker(Client client, Session session, Duration timeLimit) {
        super(client, session);
        this.timeLimit = timeLimit;
    }

    @Override
    public TimeLimitGameRoomOperator makeTimeLimitGameRoomOperator() {
        TimeLimitGameRoomOperator timeLimitGameRoomOperator = new TimeLimitGameRoomOperator(client, session, timeLimit);
        boolean result = timeLimitGameRoomOperator.createMatch();
        if (!result) {
            return null;
        }
        Match match = timeLimitGameRoomOperator.getMatch().get();
        writeRoomInfo(match.getMatchId());
        return timeLimitGameRoomOperator;
    }

    @Override
    public boolean writeRoomInfo(String matchId) {
        Gson gson = new Gson();
        // collection
        String collectionName = SaveDataNameDefine.getCollectionName(matchId);
        // data 1
        RoomInfoTimeLimit roomInfoTimeLimit = new RoomInfoTimeLimit(timeLimit.getSeconds());
        StorageObjectWrite saveGameObject = new StorageObjectWrite(
                collectionName,
                SaveDataNameDefine.getDataRoomInfoKey(),
                gson.toJson(roomInfoTimeLimit),
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
}
