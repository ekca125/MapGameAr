package com.ekcapaper.racingar.stub;

import android.location.Location;

import com.ekcapaper.racingar.modelgame.address.MapRange;

public class LocationStub {
    public static double startLatitude = 35.0979529784;
    public static double startLongitude = 129.0219886069;
    public static double endLatitude = 35.1066801454;
    public static double endLongitude = 129.0290353612;
    public static double latitude = 35.0979529784;
    public static double longitude = 129.0219886069;
    public static Location location;
    static{
        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        mapRange = MapRange.calculateMapRange(location,1);
    }
    public static MapRange mapRange;
}
