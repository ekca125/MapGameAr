package com.ekcapaper.racingar.retrofit;

import com.ekcapaper.racingar.retrofit.dto.MapRange;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressMapService {
    @GET("/api/v1/address/id={id}")
    Call<String> findAddress(@Path("id") long id);

    @GET("/api/v1/address/draw/random")
    Call<String> drawRandom();

    @POST("/api/v1/address/draw/range")
    Call<String> drawMapRangeRandom(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-10")
    Call<String> drawMapRangeRandom10(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-50")
    Call<String> drawMapRangeRandom50(@Body MapRange mapRange);

    @POST("/api/v1/address/draw/range-limit-100")
    Call<String> drawMapRangeRandom100(@Body MapRange mapRange);
}
