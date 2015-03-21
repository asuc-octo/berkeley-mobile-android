package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/*
 * The Stop class can signify either a bus stop or a transfer point. Transfer points lack a location
 * and have an extra field for their previous and next stop times.
 */
public class Stop {

    private int id;
    private String name;
    private LatLng location;

    private Date startTime;
    private Date endTime;

    public Stop(int id, String name, LatLng location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Stop(String name, Date startTime, Date endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviatedName() {
        return name.split(":|;")[0];
    }

    public LatLng getLocation() {
        return location;
    }

    public boolean isTransfer() {
        return location == null;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getPreviousArrival() {
        return endTime;
    }

}
