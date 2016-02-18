package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.Date;

public class Library {

    private int id;
    private String name;
    private String location;
    private String phone;
    private Date opening;
    private Date closing;
    private Date[] weeklyOpen;
    private Date[] weeklyClose;
    private String imageUrl;
    private LatLng latLng;
    private boolean byAppointment;
    private boolean[] weeklyAppointments;
    private int weekday;
    private final static String[] weekdays = {"Sunday   ","Monday   ","Tuesday  ","Wednesday","Thursday ","Friday   ","Saturday "};

    public Library(int id, String name, String location, String phone, Date opening,
                   Date closing, Date[] weeklyOpen, Date[] weeklyClose,
                   String imageUrl, double lat, double lng,
                   boolean byAppointment, boolean[] weeklyAppointments, int weekday) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.opening = opening;
        this.closing = closing;
        this.imageUrl = imageUrl;
        this.latLng = new LatLng(lat, lng);
        this.byAppointment = byAppointment;
        this.weeklyOpen = weeklyOpen;
        this.weeklyClose = weeklyClose;
        this.weeklyAppointments = weeklyAppointments;
        this.weekday = weekday;
    }

    public int getId() {
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

    public Date[] getWeeklyOpen() { return weeklyOpen; }

    public Date[] getWeeklyClose() { return weeklyClose; }

    public String getImageUrl() {
        return imageUrl;
    }

    public LatLng getCoordinates() {
        return latLng;
    }

    public boolean isByAppointment() {
        return byAppointment;
    }

    public boolean[] getWeeklyAppointments() { return weeklyAppointments; }

    public int getWeekday() { return weekday; }

    public String[] getWeekdays() {
        return weekdays;
    }

    public String getDayOfWeek(int i) {

        return weekdays[(i + weekday) % 7];
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

    public boolean isOpenGivenTimes(Date i, Date j) {
        if (i == null || j == null) {
            return false;
        }

        Date currentTime = new Date();
        return true;
    }

    public String weeklyHours() {
        return weekday + "RESISTANCE IS FUTILE\nMERRY EASTER";
    }

}