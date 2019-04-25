package com.asuc.asucmobile.domain.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class Gym {

    public static final String TAG = "Gym";

    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("EEEE", Locale.ENGLISH);

    private int id;
    private String name;
    private String address;
    @SerializedName("opening_time_today")
    private Date opening;
    @SerializedName("closing_time_today")
    private Date closing;
    @SerializedName("weekly_opening_times")
    private ArrayList<Date> weeklyOpen;
    @SerializedName("weekly_closing_times")
    private ArrayList<Date> weeklyClose;

    private boolean byAppointment;


    @SerializedName("weekly_by_appointment")
    private ArrayList<Boolean> weeklyAppointments;

    @SerializedName("image_link")
    private String imageUrl;
    /**
     * Outputs a day of week as a string with spaces padded at the end to be equal length for all
     * days.
     */
    public String getDayOfWeek(int i) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // Aligns weekday with i. i was range [0,6] and weekday was range [1,7].
        return String.format("%1$-" + MAX_DAY_LENGTH + "s", WEEKDAYS[(i + day - 1) % 7]);
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
    public String toString() {
        return "Gym{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + address + '\'' +
                ", opening=" + opening +
                ", closing=" + closing +
                ", weeklyOpen=" + weeklyOpen.toString() +
                ", weeklyClose=" + weeklyClose.toString() +
                '}';
    }

}