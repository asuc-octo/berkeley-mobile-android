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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Library implements Comparable<Library>{

    public static final String TAG = "Library";

    public static final double INVALID_COORD = -181;
    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);


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
    private boolean byAppointment;
    private boolean hasCoordinates;

    @SerializedName("weekly_by_appointment")
    private ArrayList<Boolean> weeklyAppointments;
    private int weekday;

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
        return byAppointment;
    }

    public ArrayList<Boolean> getWeeklyAppointments() { return weeklyAppointments; }

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
        boolean isOpen = (compareTimes(currentTime, opening) >= 0) && ((compareTimes(currentTime, closing) <= 0));
//        Log.d("isopen", currentTime.toString() + ", " + opening.toString() + ", " + closing.toString());
//        Log.d("compareTimes", Long.toString(compareTimes(currentTime, opening)) + ", " + Long.toString(compareTimes(currentTime, opening)));
        return isOpen;
    }

    public long compareTimes(Date d1, Date d2) {
        long     t1;
        long     t2;
        t1 = d1.getHours()*60*60+d1.getMinutes()*60+d1.getSeconds();
        t2 = d2.getHours()*60*60+d2.getMinutes()*60+d2.getSeconds();
        long diff = t1-t2;
        System.out.println("t1 - "+t1+", t2 - "+t2+",  diff - "+diff);

        return diff;
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