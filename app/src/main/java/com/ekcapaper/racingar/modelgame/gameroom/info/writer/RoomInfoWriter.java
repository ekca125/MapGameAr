package com.ekcapaper.racingar.modelgame.gameroom.info.writer;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.info.RoomInfo;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjectAcks;
import com.heroiclabs.nakama.api.StorageObjects;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RoomInfoWriter {
    private final Client client;
    private final Session session;
    private final String matchId;
    //
    private final String collectionName;
    private final String keyName;

    public RoomInfoWriter(Client client, Session session, String matchId) {
        this.client = client;
        this.session = session;
        this.matchId = matchId;
        //
        this.collectionName = RoomDataSpace.getCollectionName(matchId);
        this.keyName = RoomDataSpace.getDataRoomInfoKey();
    }


    public boolean writeRoomInfo(RoomInfo roomInfo){
        Gson gson = new Gson();
        // 정보
        String collectionName = RoomDataSpace.getCollectionName(matchId);
        String keyName = RoomDataSpace.getDataRoomInfoKey();
        String roomInfoJson = gson.toJson(roomInfo);
        try {
            // 쓰기
            StorageObjectWrite storageObjectWrite = new StorageObjectWrite(
                    collectionName,
                    keyName,
                    roomInfoJson,
                    PermissionRead.PUBLIC_READ,
                    PermissionWrite.OWNER_WRITE
            );
            StorageObjectAcks acks = client.writeStorageObjects(session, storageObjectWrite).get();
            return true;
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            //e.printStackTrace();
            return false;
        }
    }
}
