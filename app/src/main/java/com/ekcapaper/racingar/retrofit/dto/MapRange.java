package com.ekcapaper.racingar.retrofit.dto;

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
}
