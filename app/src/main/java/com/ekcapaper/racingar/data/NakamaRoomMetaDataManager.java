package com.ekcapaper.racingar.data;

import com.heroiclabs.nakama.api.Group;

import java.util.Map;

public class NakamaRoomMetaDataManager { 
    private final NakamaNetworkManager nakamaNetworkManager;
    //
    private static final String collectionName = "gameRoom";
    private static final String keyName = "metadata";
    public NakamaRoomMetaDataManager(NakamaNetworkManager nakamaNetworkManager){
        this.nakamaNetworkManager = nakamaNetworkManager;
    }

    public boolean writeRoomMetaDataSync(Group group, Map<String,Object> metadata){
        if(!group.getCreatorId().equals(nakamaNetworkManager.getCurrentSessionUserId())){
            throw new IllegalStateException("방을 만든 사용자만이 데이터를 쓸 수 있습니다.");
        }
        return nakamaNetworkManager.writePublicServerStorageSync(collectionName,keyName,metadata);
    }

    public Map<String,Object> readRoomMetaDataSync(Group group){
        return nakamaNetworkManager.readServerStorageSync(collectionName,keyName,group.getCreatorId());
    }

}
