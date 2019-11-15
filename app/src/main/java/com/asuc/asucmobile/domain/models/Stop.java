package com.asuc.asucmobile.domain.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by alexthomas on 10/10/17.
 */

public class Stop implements java.io.Serializable {
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private int id;
    private String name;
    private String abbreviatedName;
    private LatLng location;
    private Date startTime;
    private Date endTime;
    private String distance = "";
    private double latitude;
    private double longitude;


    public Stop(int id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.abbreviatedName = name.split(":|;")[0];
        this.latitude = lat;
        this.longitude = lon;
    }

    public Stop(String name, Date startTime, Date endTime) {
        this.name = name;
        this.abbreviatedName = name.split(":|;")[0];
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    public LatLng getLocation() {
        return location;
    }

    public boolean isTransfer() {
        return this.latitude == 0 && this.longitude == 0;
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

    public void setID(int id){
        this.id = id;
    }

    public void setDistance(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(lat, lng, location.latitude, location.longitude, results);
        distance = DECIMAL_FORMAT.format(results[0] * 0.000621371192);
    }

    @Override
    public boolean equals(Object object) {
        return this.getId() == ((Stop) object).getId();

    }

}
