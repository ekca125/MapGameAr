package com.ekcapaper.racingar.retrofit;

import com.ekcapaper.racingar.retrofit.dto.AddressDto;
import com.ekcapaper.racingar.modelgame.address.MapRange;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressMapService {
    @GET("/api/v1/address/id={id}")
    Call<AddressDto> findAddress(@Path("id") long id);

    @GET("/api/v1/address/draw/random")
    Call<AddressDto> drawRandom();

    @POST("/api/v1/address/draw/range")
    Call<List<AddressDto>> drawMapRangeRandom(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-10")
    Call<List<AddressDto>> drawMapRangeRandom10(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-50")
    Call<List<AddressDto>> drawMapRangeRandom50(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-100")
    Call<List<AddressDto>> drawMapRangeRandom100(@Body MapRange mapRange);
}
