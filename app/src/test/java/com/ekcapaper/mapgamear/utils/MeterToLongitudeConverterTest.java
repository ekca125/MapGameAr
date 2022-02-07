package com.ekcapaper.mapgamear.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MeterToLongitudeConverterTest {
    @Test
    public void convertMeterToLongitude() {
        MeterToLongitudeConverter meterToLongitudeConverter = new MeterToLongitudeConverter(37);
        double longitude = meterToLongitudeConverter.convertKilometerToLongitude(1);
        assertTrue(longitude <= 1);
    }
}