package com.ekcapaper.racingar.modelgame.gameroom.prepare.writer;

import com.ekcapaper.racingar.modelgame.gameroom.RoomDataSpace;
import com.ekcapaper.racingar.modelgame.gameroom.prepare.PrepareDataFlagGameRoom;
import com.google.gson.Gson;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObjectAcks;

import java.util.concurrent.ExecutionException;

public class PrepareDataFlagGameRoomWriter extends PrepareDataWriter{
    Gson gson;
    public PrepareDataFlagGameRoomWriter(Client client, Session session, String matchId) {
        super(client, session, matchId);
        this.gson = new Gson();
    }

    public boolean writePrepareData(PrepareDataFlagGameRoom prepareDataFlagGameRoom){
        // 정보
        String prepareDataFlagGameRoomJson = gson.toJson(prepareDataFlagGameRoom);
        try {
            // 쓰기
            StorageObjectWrite storageObjectWrite = new StorageObjectWrite(
                    collectionName,
                    keyName,
                    prepareDataFlagGameRoomJson,
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
