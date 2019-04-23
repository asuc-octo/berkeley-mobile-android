package com.asuc.asucmobile.domain.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.Builder;

@Builder
public class Library implements Comparable<Library>{

    public static final String TAG = "Library";

    public static final double INVALID_COORD = -181;
    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


    private int id;
    private String name;
    @SerializedName("campus_location")
    private String location;
    @SerializedName("phone_number")
    private String phone;
    @SerializedName("opening_time_today")
    private Date opening;
    @SerializedName("closing_time_today")
    private Date closing;
    @SerializedName("weekly_opening_times")
    private ArrayList<Date> weeklyOpen;
    @SerializedName("weekly_closing_times")
    private ArrayList<Date> weeklyClose;
    private LatLng latLng;
    private double latitude;
    private double longitude;
    private boolean hasCoordinates;

    @SerializedName("weekly_by_appointment")
    private ArrayList<Boolean> weeklyAppointments;
    private int weekday;


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
        int i = getIdx();
        if (i < 0) {
            i = 0;
        }
        return weeklyOpen.get(i);
    }

    public Date getClosing() {
        int i = getIdx();
        if (i < 0) {
            i = 0;
        }
        return weeklyClose.get(i);
    }

    public ArrayList<Date> getWeeklyOpen() { return weeklyOpen; }

    public ArrayList<Date> getWeeklyClose() { return weeklyClose; }

    public LatLng getCoordinates() {
        if (latLng == null) {
            if (latitude == INVALID_COORD || longitude == INVALID_COORD) {
                hasCoordinates = false;
            } else {
                hasCoordinates = true;
                this.latLng = new LatLng(latitude, longitude);
            }
        }
        return hasCoordinates ? latLng : null;
    }

    public boolean isByAppointment() {
        int i = getIdx();
        if (i < 0) {
            i = 0;
        }
        return weeklyAppointments.get(i);
    }

    public ArrayList<Boolean> getWeeklyAppointments() { return weeklyAppointments; }

    /**
     * Outputs a day of week as a string with spaces padded at the end to be equal length for all
     * days.
     */
    public String getDayOfWeek(int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(weeklyOpen.get(i));
        int day = c.get(Calendar.DAY_OF_WEEK);
        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(weeklyOpen.get(i));
        // Aligns weekday with i. i was range [0,6] and weekday was range [1,7].
        return String.format("%1$-" + MAX_DAY_LENGTH + "s", dayName);
    }

    private int getIdx() {
        Calendar c = Calendar.getInstance();
        int dayNum = c.get(Calendar.DAY_OF_WEEK);
        int i = 0;
        for (i = 0; i < weeklyOpen.size(); i++) {
            c.setTime(weeklyOpen.get(i));
            if (c.get(Calendar.DAY_OF_WEEK) == dayNum) {
                return i;
            }

        }
        return -1;
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the library is open or not.
     */
    public boolean isOpen() {

        Date currentTime = new Date();

        int i = getIdx();
        if (i < 0) {
            return false;
        }

        Date opening = weeklyOpen.get(i);
        Date closing = weeklyClose.get(i);

        if (opening == null || closing == null) {
            return false;
        }

        /* Open 24/7 */
        if (opening.equals(closing)) {
            return true;
        }
        boolean isOpen = currentTime.after(opening) && currentTime.before(closing);
        return isOpen;
    }

    @Override
    public int compareTo(@NonNull Library other) {
        return this.getName().compareTo(other.getName());
    }


    @Override
    public String toString() {
        return "Library{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", phone='" + phone + '\'' +
                ", opening=" + opening +
                ", closing=" + closing +
                ", weeklyOpen=" + weeklyOpen.toString() +
                ", weeklyClose=" + weeklyClose.toString() +
                ", latLng=" + latLng +
                ", weekday=" + weekday +
                '}';
    }
}