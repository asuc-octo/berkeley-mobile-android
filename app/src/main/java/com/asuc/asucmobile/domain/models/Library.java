package com.asuc.asucmobile.domain.models;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

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

    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("EEEE", Locale.ENGLISH);


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
        Pair<Integer, Integer> i = getIdxList();
        int x = i.first;
        if (i.first < 0) {
            x = 0;
        }
        return weeklyAppointments.get(x);
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

    private Pair<Integer, Integer> getIdxList() {
        String today = DAY_FORMAT.format(new Date());
        int one = -1, two = -1;
        boolean startAssigned = false;
        for (int i = 0; i < weeklyOpen.size(); i++) {
            String currDay = DAY_FORMAT.format(weeklyOpen.get(i));
            if (today.equals(currDay)) {
                if (!startAssigned) {
                    startAssigned = true;
                    one = i;
                    two = i;
                } else {
                    two = i;
                }
            }
        }
        return new Pair<>(one, two);
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the library is open or not.
     */
    public boolean isOpen() {

        Date currentTime = new Date();

        Pair<Integer, Integer> i = getIdxList();

        if (i.first < 0) {
            return false;
        }

        boolean isOpen = false;

        for (int x = i.first; x <= i.second; x++) {
            Date opening = weeklyOpen.get(x);
            Date closing = weeklyClose.get(x);

            if (opening == null || closing == null) {
                return false;
            }

            /* Open 24/7 */
            if (opening.equals(closing)) {
                return true;
            }
            isOpen = isOpen || currentTime.after(opening) && currentTime.before(closing);
        }

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