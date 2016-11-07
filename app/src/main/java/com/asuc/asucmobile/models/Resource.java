package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Resource implements Comparable<Resource>{

    public static final double INVALID_COORD = -181;

    private boolean hasCoordinates;
    private String resource;
    private String topic;
    private String phone1;
    private String phone2;
    private String location;
    private String hours;
    private String email;
    private String onOrOffCampus;
    private String notes;
    private LatLng latLng;

    public Resource(String resource, String topic, String phone1, String phone2, String location,
                    String hours, String email, String onOrOffCampus, double lat, double lng,
                    String notes) {
        this.resource = resource;
        this.topic = topic;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.location = location;
        this.hours = hours;
        this.email = email;
        this.onOrOffCampus = onOrOffCampus;
        this.notes = notes;
        if (lat == INVALID_COORD || lng == INVALID_COORD) {
            hasCoordinates = false;
        } else {
            hasCoordinates = true;
            this.latLng = new LatLng(lat, lng);
        }
    }

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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getEmail() {
        return email;
    }

    public String getOnOrOffCampus() {
        return onOrOffCampus;
    }

    public LatLng getCoordinates() {
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
