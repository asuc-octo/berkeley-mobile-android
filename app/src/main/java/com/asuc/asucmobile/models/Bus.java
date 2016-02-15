package com.asuc.asucmobile.models;


import com.google.android.gms.maps.model.LatLng;

public class Bus {

    private int id;
    private LatLng location;
    private int lineId;
    private double speed;
    private double bearing;
    private int vehicleId;
    private String lineName;
    private boolean inService;

    public Bus(int id, LatLng location, int lineId, double speed, double bearing, int vehicleId,
               String lineName, boolean inService) {
        this.id = id;
        this.location = location;
        this.lineId = lineId;
        this.speed = speed;
        this.bearing = bearing;
        this.vehicleId = vehicleId;
        this.lineName = lineName;
        this.inService = inService;
    }

    public int getId() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    public int getLineId() {
        return lineId;
    }

    public double getSpeed() {
        return speed;
    }

    public double getBearing() {
        return bearing;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getLineName() {
        return lineName;
    }

    public boolean isInService() {
        return inService;
    }
}
