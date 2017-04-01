package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Gym {

    public static final double INVALID_COORD = -181;

    private int id;
    private String name;
    private String address;
    private Date opening;
    private Date closing;
    private String imageUrl;
    private LatLng latLng;
    private boolean hasCoordinates;

    public Gym(int id, String name, String address, Date opening, Date closing, String imageUrl, double lat, double lng) {
        this.id = id;
        this.name = name.replace("\n", "").trim();
        this.address = address;
        this.opening = opening;
        this.closing = closing;
        this.imageUrl = imageUrl;
        if (lat == INVALID_COORD || lng == INVALID_COORD) {
            hasCoordinates = false;
        } else {
            hasCoordinates = true;
            this.latLng = new LatLng(lat, lng);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Date getOpening() {
        return opening;
    }

    public Date getClosing() {
        return closing;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LatLng getCoordinates() {
        return hasCoordinates ? latLng : null;
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the gym is open or not.
     */
    public boolean isOpen() {
        if (opening == null || closing == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(opening) && currentTime.before(closing);
    }

}
