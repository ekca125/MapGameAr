package com.ekcapaper.racingar.modelgame.gameroom.info.reader;

import android.util.Log;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
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

public class RoomInfoReader {
    private final Client client;
    private final Session session;
    private final String matchId;

    public RoomInfoReader(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = RoomDataSpace.normalizeMatchId(matchId);
    }

    public RoomInfo readRoomInfo(){
        Gson gson = new Gson();
        // 정보
        String collectionName = RoomDataSpace.getCollectionName(matchId);
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
                    .filter(storageObject -> storageObject.getKey().equals(RoomDataSpace.getDataRoomInfoKey()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getValue();
            Log.d("InfoRead",jsonData);

            // 변환
            return gson.fromJson(jsonData,RoomInfo.class);
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            //e.printStackTrace();
            Log.d("InfoRead",e.toString());
            return null;
        }
    }
}
