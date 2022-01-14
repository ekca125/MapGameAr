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
    private Consumer<Location> runFunction;

    public LocationRequestSpaceUpdater(Context context, Consumer<Location> runFunction) {
        super(context);
        this.running = false;
        this.timer = null;
        this.timerTask = null;
        this.runFunction = runFunction;
    }

    public void start(){
        if(!running){
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
    }

    public void stop(){
        if(running) {
            running = false;
            timerTask.cancel();
            timerTask = null;
            timer.cancel();
            timer = null;
        }
    }
}
