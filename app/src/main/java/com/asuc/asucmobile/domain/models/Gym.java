package com.asuc.asucmobile.domain.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    private static final int MAX_DAY_LENGTH = 9;
    private static final String[] WEEKDAYS =
            { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


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
        boolean isOpen = currentTime.after(opening) && currentTime.before(closing);
        Log.d("isopen", currentTime.toString() + ", " + opening.toString() + ", " + closing.toString());
        return isOpen;
    }

    private String parseTimes(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(opening) + " - " + df.format(closing);
    }

}