package com.ekcapaper.mapgamear.modelgame.address;

import android.location.Location;

import com.ekcapaper.mapgamear.utils.MeterToLatitudeConverter;
import com.ekcapaper.mapgamear.utils.MeterToLongitudeConverter;

import lombok.Builder;

public class MapRange {
    double startLatitude;
    double startLongitude;
    double endLatitude;
    double endLongitude;

    @Builder
    public MapRange(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    static public MapRange calculateMapRange(Location mapCenter, double mapLengthKilometer) {
        double currentLatitude = mapCenter.getLatitude();
        double currentLongitude = mapCenter.getLongitude();

        MeterToLatitudeConverter meterToLatitudeConverter = new MeterToLatitudeConverter();
        MeterToLongitudeConverter meterToLongitudeConverter = new MeterToLongitudeConverter(currentLatitude);

        double distanceKilometer = mapLengthKilometer / 2;

        double halfHeightLatitude = meterToLatitudeConverter.convertKiloMeterToLatitude(distanceKilometer);
        double halfWidthLongitude = meterToLongitudeConverter.convertKilometerToLongitude(distanceKilometer);

        double startLatitude = currentLatitude - halfHeightLatitude;
        double startLongitude = currentLongitude - halfWidthLongitude;
        double endLatitude = currentLatitude + halfHeightLatitude;
        double endLongitude = currentLongitude + halfWidthLongitude;

        return new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    @Override
    public String toString() {
        return "MapRange{" +
                "startLatitude=" + startLatitude +
                ", startLongitude=" + startLongitude +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                '}';
    }

    public Location getMapCenter() {
        Location location = new Location("");
        double latitude = (Math.abs(startLatitude) + Math.abs(endLatitude)) / 2;
        double longitude = (Math.abs(startLongitude) + Math.abs(endLongitude)) / 2;

        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }
}
