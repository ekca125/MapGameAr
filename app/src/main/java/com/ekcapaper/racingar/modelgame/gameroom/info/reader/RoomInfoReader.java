package com.ekcapaper.racingar.modelgame.gameroom.info.reader;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RoomInfoReader {
    private final Client client;
    private final Session session;
    private final String matchId;
    //
    private final String collectionName;
    private final String keyName;

    public RoomInfoReader(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = matchId;
        //
        this.collectionName = RoomDataSpace.getCollectionName(matchId);
        this.keyName = RoomDataSpace.getDataRoomInfoKey();
    }

    public RoomInfo readRoomInfo(){
        // 준비
        StorageObjectId objectId = new StorageObjectId(collectionName);
        objectId.setKey(keyName);
        objectId.setUserId(session.getUserId());
        try {
            // 가져오기
            StorageObjects objects = client.readStorageObjects(session, objectId).get();
            StorageObject object = objects.getObjects(0);
            String jsonData = object.getValue();
            // 변환
            Gson gson = new Gson();
            return gson.fromJson(jsonData,RoomInfo.class);
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
