package com.ekcapaper.racingar.retrofit;

import static org.junit.Assert.*;

import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.modelgame.address.MapRange;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class AddressMapServiceTest {
    @Test
    public void findAddress() throws IOException {
        Call<AddressDto> requester = AddressMapClient.getMapAddressService().findAddress(1);
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }

    @Test
    public void drawRandom() throws IOException {
        Call<AddressDto> requester = AddressMapClient.getMapAddressService().drawRandom();
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }

    @Test
    public void drawMapRange() throws IOException {
        double startLatitude = 35.0979529784;
        double startLongitude = 129.0219886069;
        double endLatitude = 35.1066801454;
        double endLongitude = 129.0290353612;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom(mapRange);
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }

    @Test
    public void drawMapRangePrint() throws IOException {
        double startLatitude = 35.0979529784;
        double startLongitude = 129.0219886069;
        double endLatitude = 35.1066801454;
        double endLongitude = 129.0290353612;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom(mapRange);
        //System.out.println(requester.execute().body());
    }

    @Test
    public void drawMapRangeRandom10() throws IOException {
        double startLatitude = 35.0979529784;
        double startLongitude = 129.0219886069;
        double endLatitude = 35.1066801454;
        double endLongitude = 129.0290353612;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom10(mapRange);
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }

    @Test
    public void drawMapRangeRandom50() throws IOException {
        double startLatitude = 35.0979529784;
        double startLongitude = 129.0219886069;
        double endLatitude = 35.1066801454;
        double endLongitude = 129.0290353612;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom50(mapRange);
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }

    @Test
    public void drawMapRangeRandom100() throws IOException {
        double startLatitude = 35.0979529784;
        double startLongitude = 129.0219886069;
        double endLatitude = 35.1066801454;
        double endLongitude = 129.0290353612;

        MapRange mapRange = new MapRange(startLatitude, startLongitude, endLatitude, endLongitude);
        Call<List<AddressDto>> requester = AddressMapClient.getMapAddressService().drawMapRangeRandom100(mapRange);
        boolean result = requester.execute().isSuccessful();
        assertTrue(result);
    }
}