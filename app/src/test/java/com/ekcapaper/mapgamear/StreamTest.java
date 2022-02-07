package com.ekcapaper.mapgamear;

import org.junit.Test;

import java.util.Arrays;

public class StreamTest {
    @Test
    public void compareTest() {
        Double[] array = new Double[2];
        array[0] = 1.0;
        array[1] = 2.0;
        Arrays.stream(array).sorted((a, b) -> {
            return Double.compare(b, a);
        }).forEach(System.out::println);
    }
}
