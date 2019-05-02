package com.asuc.asucmobile.domain.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Resource implements Comparable<Resource>{

    public static final double INVALID_COORD = -181;

    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


    private boolean hasCoordinates;

    @SerializedName("Resource")
    private String resource;
    @SerializedName("Topic")
    private String topic;
    @SerializedName("Phone 1")
    private String phone1;
    @SerializedName("Phone 2 (Optional)")
    private String phone2;
    @SerializedName("Office Location")
    private String location;
    @SerializedName("Hours")
    private String hours;

    private ArrayList<Date> weeklyOpen;
    private ArrayList<Date> weeklyClose;
    private boolean byAppointment;
    private ArrayList<Boolean> weeklyAppointments;
    private Date opening;
    private Date closing;

    @SerializedName("Email")
    private String email;
    @SerializedName("On/Off Campus")
    private String onOrOffCampus;
    @SerializedName("Notes")
    private String notes;
    @SerializedName("Description")
    private String description;

    @SerializedName("Latitude")
    private double latitude;
    @SerializedName("Longitude")
    private double longitude;
    private LatLng latLng;

    /**
     * Outputs a day of week as a string with spaces padded at the end to be equal length for all
     * days.
     */
    public String getDayOfWeek(int i) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // Aligns weekday with i. i was range [0,6] and weekday was range [1,7].
        return String.format("%1$-" + MAX_DAY_LENGTH + "s", WEEKDAYS[(i + day - 1) % 7]);
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the library is open or not.
     */
    public boolean isOpen() {

        // infra layer ensures always non null
        if (weeklyOpen.isEmpty() || weeklyClose.isEmpty()) {
            return false;
        }

        Date opening = weeklyOpen.get(0);
        Date closing = weeklyClose.get(0);

        /* Open 24/7 */
        if (opening.equals(closing)) {
            return true;
        }
        Date currentTime = new Date();
        boolean isOpen = currentTime.after(opening) && currentTime.before(closing);
        return isOpen;
    }

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

    @Override
    public int compareTo(@NonNull Resource other) {
        return this.getResource().compareTo(other.getResource());
    }

}