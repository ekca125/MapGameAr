package com.ekcapaper.racingar.data;

import androidx.room.Index;

import com.heroiclabs.nakama.api.Group;

import java.util.Map;

public class NakamaRoomPreparedDataManager {
    private final NakamaNetworkManager nakamaNetworkManager;
    //
    private static final String collectionName = "gameRoom";
    private static final String keyName = "prepareData";
    public NakamaRoomPreparedDataManager(NakamaNetworkManager nakamaNetworkManager){
        this.nakamaNetworkManager = nakamaNetworkManager;
    }

    public boolean writeRoomPrepareDataSync(Group group, Map<String,Object> metadata){
        if(!group.getCreatorId().equals(nakamaNetworkManager.getCurrentSessionUserId())){
            throw new IllegalStateException("방을 만든 사용자만이 데이터를 쓸 수 있습니다.");
        }
        return nakamaNetworkManager.writePublicServerStorageSync(collectionName,keyName,metadata);
    }

    public Map<String,Object> readRoomPrepareDataSync(Group group){
        return nakamaNetworkManager.readServerStorageSync(collectionName,keyName,group.getCreatorId());
    }

    public Map<String,Object> readRoomPrepareDataSync(String groupName){
        try {
            Group group = nakamaNetworkManager.getGroupList(groupName).getGroups(0);
            return nakamaNetworkManager.readServerStorageSync(collectionName, keyName, group.getCreatorId());
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
    }
}
