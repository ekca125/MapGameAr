package com.ekcapaper.racingar.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class MeterConverterTest {
    @Test
    public void convertKilometerToMeter() {
        MeterConverter meterConverter = new MeterConverter();
        double meter = meterConverter.convertKilometerToMeter(1);
        assertTrue(meter <= 1000);
    }

    @Test
    public void convertMeterToKiloMeter() {
        MeterConverter meterConverter = new MeterConverter();
        double kilometer = meterConverter.convertMeterToKiloMeter(1000);
        assertTrue(kilometer <= 1);
    }
}