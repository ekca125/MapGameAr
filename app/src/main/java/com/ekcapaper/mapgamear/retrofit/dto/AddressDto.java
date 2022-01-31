package com.ekcapaper.mapgamear.retrofit.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddressDto {
    @SerializedName("id")
    long id;
    @SerializedName("latitude")
    double latitude;
    @SerializedName("longitude")
    double longitude;

    @Builder
    public AddressDto(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
