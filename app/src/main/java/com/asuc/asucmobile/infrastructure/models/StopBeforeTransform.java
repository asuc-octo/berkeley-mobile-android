package com.asuc.asucmobile.infrastructure.models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class StopBeforeTransform {
    @SerializedName("id")
    private int stopId;

    @SerializedName("name")
    private String stopName;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public int getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
