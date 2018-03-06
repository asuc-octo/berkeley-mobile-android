package com.asuc.asucmobile.models;

/**
 * Created by alexthomas on 5/26/17.
 */

public class Vehicle {
    private String id;
    private String block;
    private String trip;

    Vehicle(String id, String block, String trip) {
        this.id = id;
        this.block = block;
        this.trip = trip;
    }

    public String getId() {
        return id;
    }

    public String getBlock() {
        return block;
    }

    public String getTrip() {
        return trip;
    }
}
