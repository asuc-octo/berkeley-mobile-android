package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Route {

    private String[] stops;
    private Date startTime;
    private Date endTime;
    private LatLng destination;

    public Route (String[] stops, Date startTime, Date endTime, LatLng destination) {
        this.stops = stops;
        this.startTime = startTime;
        this.endTime = endTime;
        this.destination = destination;
    }

    public String[] getStops() {
        return stops;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public LatLng getDestination() {
        return destination;
    }

}
