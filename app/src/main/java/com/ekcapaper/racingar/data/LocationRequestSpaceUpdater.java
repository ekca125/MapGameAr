package com.ekcapaper.racingar.data;

import android.content.Context;
import android.location.Location;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class LocationRequestSpaceUpdater extends LocationRequestSpace{
    private boolean running;
    private Timer timer;
    private TimerTask timerTask;

    public LocationRequestSpaceUpdater(Context context) {
        super(context);
        running = false;
        timer = null;
        timerTask = null;
    }

    public void start(Consumer<Location> runFunction){
        if(running){
            throw new IllegalStateException();
        }
        running = true;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getCurrentLocation().ifPresent(runFunction);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void stop(){
        if(!running){
            throw new IllegalStateException();
        }
        running = false;
        timerTask.cancel();
        timerTask = null;
        timer.cancel();
        timer = null;
    }
}
