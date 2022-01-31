package com.ekcapaper.mapgamear.utils;

public class MeterToLatitudeConverter extends MeterConverter {
    // 근사값
    // 위도 1분 = 111.32 km
    // 경도 1분 = 40075 km * cos( latitude ) / 360

    // 한국 (위도 = 37)
    // 위도 1분 = 111.32 km
    // 경도 1분 = 88.90km

    // 위도 1초 = 0.0309 km = 30m
    // 경도 1초 = 0.024km = 24m
    final private double latitudeOneMinuteKiloMeter;
    final private double latitudeOneMinuteMeter;
    final private double latitudeOneSecondKiloMeter;
    final private double latitudeOneSecondMeter;

    public MeterToLatitudeConverter() {
        latitudeOneMinuteKiloMeter = 111.32;
        latitudeOneMinuteMeter = convertKilometerToMeter(latitudeOneMinuteKiloMeter);
        latitudeOneSecondKiloMeter = latitudeOneMinuteKiloMeter / 3600;
        latitudeOneSecondMeter = convertKilometerToMeter(latitudeOneSecondKiloMeter);
    }

    public double convertMeterToLatitude(double meter) {
        return meter * latitudeOneSecondMeter;
    }

    public double convertKiloMeterToLatitude(double kilometer) {
        return kilometer * latitudeOneSecondKiloMeter;
    }
}