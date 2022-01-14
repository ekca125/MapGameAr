package com.ekcapaper.racingar.data;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

public class LocationRequestSpaceUpdater extends LocationRequestSpace{
    boolean checkAndUpdateStatus;
    Timer checkTimer;
    TimerTask endCheckTimerTask;
    public LocationRequestSpaceUpdater(Context context) {
        super(context);


    }
}
