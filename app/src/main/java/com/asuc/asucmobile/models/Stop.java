package com.asuc.asucmobile.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Date;

/*
 * The Stop class can signify either a bus stop or a transfer point. Transfer points lack a location
 * and have an extra field for their previous and next stop times.
 */
public class Stop {

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private int id;
    private String name;
    private LatLng location;

    private Date startTime;
    private Date endTime;

    private String distance = "";

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(lat, lng, location.latitude, location.longitude, results);

        distance = DECIMAL_FORMAT.format(results[0] * 0.000621371192);
    }

}
