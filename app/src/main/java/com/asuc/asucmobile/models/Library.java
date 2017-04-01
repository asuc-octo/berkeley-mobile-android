package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Library implements Comparable<Library>{

    public static final double INVALID_COORD = -181;
    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    private int id;
    private String name;
    private String location;
    private String phone;
    private Date opening;
    private Date closing;
    private Date[] weeklyOpen;
    private Date[] weeklyClose;
    private LatLng latLng;
    private boolean byAppointment;
    private boolean hasCoordinates;
    private boolean[] weeklyAppointments;
    private int weekday;
    private String imageUrl;

    public Library(int id, String name, String location, String phone, Date opening,
                   Date closing, Date[] weeklyOpen, Date[] weeklyClose, double lat, double lng,
                   boolean byAppointment, boolean[] weeklyAppointments, int weekday, String imgUrl) {
        this.id = id;
        this.name = name.replace("\n", "").trim();
        this.location = location;
        this.phone = phone;
        this.opening = opening;
        this.closing = closing;
        this.byAppointment = byAppointment;
        this.weeklyOpen = weeklyOpen;
        this.weeklyClose = weeklyClose;
        this.weeklyAppointments = weeklyAppointments;
        this.weekday = weekday;
        this.imageUrl = imgUrl;
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

    public LatLng getCoordinates() {
        return hasCoordinates ? latLng : null;
    }

    public boolean isByAppointment() {
        return byAppointment;
    }

    public boolean[] getWeeklyAppointments() { return weeklyAppointments; }

    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Outputs a day of week as a string with spaces padded at the end to be equal length for all
     * days.
     */
    public String getDayOfWeek(int i) {
        // Aligns weekday with i. i was range [0,6] and weekday was range [1,7].
        return String.format("%1$-" + MAX_DAY_LENGTH + "s", WEEKDAYS[(i + weekday - 1) % 7]);
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

    @Override
    public int compareTo(@NonNull Library other) {
        return this.getName().compareTo(other.getName());
    }

}
