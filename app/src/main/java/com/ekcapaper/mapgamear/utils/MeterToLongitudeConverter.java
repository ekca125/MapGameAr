package com.ekcapaper.mapgamear.utils;

public class MeterToLongitudeConverter extends MeterConverter {
    // 근사값
    // 위도 1분 = 111.32 km
    // 경도 1분 = 40075 km * cos( latitude ) / 360

    // 한국 (위도 = 37)
    // 위도 1분 = 111.32 km
    // 경도 1분 = 88.90km

    // 위도 1초 = 0.0309 km = 30m
    // 경도 1초 = 0.024km = 24m
    final private double longitudeOneMinuteKiloMeter;
    final private double longitudeOneMinuteMeter;
    final private double longitudeOneSecondKiloMeter;
    final private double longitudeOneSecondMeter;

    public MeterToLongitudeConverter(double latitude) {
        longitudeOneMinuteKiloMeter = Math.abs(40075 * Math.cos(latitude) / 360);
        longitudeOneMinuteMeter = convertKilometerToMeter(longitudeOneMinuteKiloMeter);
        longitudeOneSecondKiloMeter = longitudeOneMinuteKiloMeter / 3600;
        longitudeOneSecondMeter = convertKilometerToMeter(longitudeOneSecondKiloMeter);
    }

    public double convertMeterToLongitude(double meter) {
        return meter * longitudeOneSecondMeter;
    }

    public double convertKilometerToLongitude(double kilometer) {
        return kilometer * longitudeOneSecondKiloMeter;
    }
}
