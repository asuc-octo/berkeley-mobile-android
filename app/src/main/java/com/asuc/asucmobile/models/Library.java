package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Library {

    private String id;
    private String name;
    private String location;
    private String phone;
    private Date opening;
    private Date closing;
    private String imageUrl;
    private LatLng latLng;
    private boolean byAppointment;

    public Library(String id, String name, String location, String phone, Date opening,
                   Date closing, String imageUrl, double lat, double lng,
                   boolean byAppointment) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.opening = opening;
        this.closing = closing;
        this.imageUrl = imageUrl;
        this.latLng = new LatLng(lat, lng);
        this.byAppointment = byAppointment;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
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
        return latLng;
    }

    public boolean isByAppointment() {
        return byAppointment;
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the library is open or not.
     */
    public boolean isOpen() {
        if (opening == null || closing == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(opening) && currentTime.before(closing);
    }

}
