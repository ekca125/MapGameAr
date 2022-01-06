package com.ekcapaper.racingar.operator.maker.joinroom;

import android.util.Log;

import com.ekcapaper.racingar.game.GameFlag;
import com.ekcapaper.racingar.operator.impl.FlagGameRoomOperator;
import com.ekcapaper.racingar.operator.maker.FlagGameRoomOperatorMaker;
import com.ekcapaper.racingar.operator.maker.ServerRoomSaveDataNameSpace;
import com.ekcapaper.racingar.retrofit.dto.MapRange;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjects;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FlagGameRoomOperatorJoinMaker extends TimeLimitGameRoomOperatorJoinMaker implements FlagGameRoomOperatorMaker {
    private MapRange mapRange;
    private List<GameFlag> gameFlagList;

    public FlagGameRoomOperatorJoinMaker(Client client, Session session, String matchId, Duration timeLimit) {
        super(client, session, matchId, timeLimit);
    }

    boolean readPrepareData(){
        StorageObjectId objectId = new StorageObjectId(ServerRoomSaveDataNameSpace.getCollectionName(matchId));
        objectId.setKey(ServerRoomSaveDataNameSpace.getRoomPrepareKeyMapRangeName());
        objectId.setUserId(session.getUserId());
        try {
            Gson gson = new Gson();

            StorageObjects objects = client.readStorageObjects(session, objectId).get();
            StorageObject object = objects.getObjects(0);
            String value = object.getValue();
            MapRange mapRange = gson.fromJson(value, MapRange.class);

            objectId.setKey(ServerRoomSaveDataNameSpace.getRoomPrepareKeyGameFlagListName());
            StorageObjects objects1 = client.readStorageObjects(session, objectId).get();
            StorageObject object1 = objects1.getObjects(0);
            value = object1.getValue();

            JsonObject jsonObject = gson.fromJson(value,JsonObject.class);
            String data = jsonObject.get("flags").toString().trim().replace("\"[","[").replace("\"]","]");
            Log.d("testtest",data);
            List<GameFlag> gameFlagList = gson.fromJson(data,new TypeToken<List<GameFlag>>(){}.getType());


        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public FlagGameRoomOperator makeFlagGameRoomOperator() {
        return null;
    }
}
