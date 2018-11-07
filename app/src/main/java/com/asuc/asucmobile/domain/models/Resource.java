package com.asuc.asucmobile.domain.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Resource implements Comparable<Resource>{

    public static final double INVALID_COORD = -181;

    private boolean hasCoordinates;

    @SerializedName("Resource")
    private String resource;
    @SerializedName("Topic")
    private String topic;
    @SerializedName("Phone 1")
    private String phone1;
    @SerializedName("Phone 2 (Optional)")
    private String phone2;
    @SerializedName("Office Location")
    private String location;
    @SerializedName("Hours")
    private String hours;
    @SerializedName("Email")
    private String email;
    @SerializedName("On/Off Campus")
    private String onOrOffCampus;
    @SerializedName("Notes")
    private String notes;
    @SerializedName("Description")
    private String description;

    @SerializedName("Latitude")
    private double latitude;
    @SerializedName("Longitude")
    private double longitude;
    private LatLng latLng;


    public String getResource() {
        return resource;
    }

    public String getTopic() {
        return topic;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getLocation() {
        return location;
    }

    public String getHours() {
        return hours;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getOnOrOffCampus() {
        return onOrOffCampus;
    }

    public LatLng getCoordinates() {
        if (latLng == null) {
            if (latitude == INVALID_COORD || longitude == INVALID_COORD) {
                hasCoordinates = false;
            } else {
                hasCoordinates = true;
                this.latLng = new LatLng(latitude, longitude);
            }
        }
        return hasCoordinates ? latLng : null;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public int compareTo(@NonNull Resource other) {
        return this.getResource().compareTo(other.getResource());
    }

}