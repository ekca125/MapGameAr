package com.ekcapaper.racingar.modelgame.gameroom.prepare.reader;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PrepareDataFlagGameRoomReader extends PrepareDataReader{
    public PrepareDataFlagGameRoomReader(Client client, Session session, String matchId) {
        super(client, session, matchId);
    }

    public PrepareDataFlagGameRoom readPrepareData(){
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
            return gson.fromJson(jsonData, PrepareDataFlagGameRoom.class);
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
