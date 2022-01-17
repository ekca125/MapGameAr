package com.ekcapaper.racingar.modelgame.gameroom.prepare.reader;

import android.util.Log;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjectList;
import com.heroiclabs.nakama.api.StorageObjects;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PrepareDataFlagGameRoomReader extends PrepareDataReader{
    public PrepareDataFlagGameRoomReader(Client client, Session session, String matchId) {
        super(client, session, matchId);
    }

    public PrepareDataFlagGameRoom readPrepareData(){
        Gson gson = new Gson();
        // 정보
        try {
            int limit = 3;
            String cursor = null;

            StorageObjectList objects = client.listUsersStorageObjects(
                    session,
                    RoomDataSpace.getCollectionName(matchId),
                    cursor,
                    limit
            ).get();

            String jsonData = objects.getObjectsList().stream()
                    .filter(storageObject -> storageObject.getKey().equals(RoomDataSpace.getDataRoomPrepareKey()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getValue();
            // 변환
            return gson.fromJson(jsonData, PrepareDataFlagGameRoom.class);
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
