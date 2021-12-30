package com.ekcapaper.racingar.retrofit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddressDto {
    long id;
    double latitude;
    double longitude;

    @Builder
    public AddressDto(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
