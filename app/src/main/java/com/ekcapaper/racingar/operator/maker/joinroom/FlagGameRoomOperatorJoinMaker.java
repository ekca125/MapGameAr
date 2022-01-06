package com.ekcapaper.racingar.operator.maker.joinroom;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FlagGameRoomOperatorJoinMaker extends TimeLimitGameRoomOperatorJoinMaker implements FlagGameRoomOperatorMaker {
    private MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session, String matchId, Duration timeLimit) {
        super(client, session, matchId, timeLimit);
    }

    private boolean readPrepareData(){
        StorageObjectId objectId = new StorageObjectId(ServerRoomSaveDataNameSpace.getCollectionName(matchId));
        objectId.setKey(ServerRoomSaveDataNameSpace.getRoomPrepareDataName());
        objectId.setUserId(session.getUserId());
        try {
            StorageObjects objects = client.readStorageObjects(session, objectId).get();
            List<StorageObject> storageObjectList = objects.getObjectsList();
            String mapRangeJson = storageObjectList.stream()
                    .filter(storageObject -> storageObject.getKey() == ServerRoomSaveDataNameSpace.getMapRangeKey())
                    .collect(Collectors.toList())
                    .get(0).getValue();
            String gameFlagListJson = storageObjectList.stream()

        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        return null;
    }
}
