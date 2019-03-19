package com.asuc.asucmobile.utilities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.models.Library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HoursStringGenerator {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    public static Spanned setUpWeeklyHoursLeft(Library library) {
        return setUpWeeklyHoursLeft(library.getWeeklyOpen());
    }

    public static Spanned setUpWeeklyHoursRight(Library library, Context context) {
        return setUpWeeklyHoursRight(library.getWeeklyOpen(), library.getWeeklyClose(), library.getWeeklyAppointments(), library.isOpen(), library.isByAppointment(), context);
    }

    /**
     * This function is called with setUpWeeklyHoursRight() to set up the weekly hours
     * page, which essentially appears as left and right justified text. The left side
     * holds days of the week and some description.
     */
    private static Spanned setUpWeeklyHoursLeft(ArrayList<Date> openings) {
        Spanned weeklyHoursString = new SpannableString("Today\n");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int today = c.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < openings.size(); i++) {
            Spannable hoursString;
            c.setTime(openings.get(i));
            String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(openings.get(i));
            hoursString = new SpannableString("\n" + dayName);
            if (today == c.get(Calendar.DAY_OF_WEEK)) {
                hoursString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, hoursString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }
        return weeklyHoursString;
    }

    /**
     * This function is called with setUpWeeklyHoursLeft() to set up the weekly hours
     * page, which essentially appears as left and right justified text. The right side
     * holds hours for each day of the week and some description.
     */
    private static Spanned setUpWeeklyHoursRight(ArrayList<Date> openings, ArrayList<Date> closings, ArrayList<Boolean> byAppointments, boolean open, boolean isByAppointment, Context context) {
        Spannable hoursString;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int today = c.get(Calendar.DAY_OF_WEEK);
        if (isByAppointment) {
            hoursString = new SpannableString("BY APPOINTMENT\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.pavan_light )), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (openings != null && closings != null) {
            String isOpen;
            int color;
            if (open) {
                isOpen = "OPEN";
                color = ContextCompat.getColor(context,R.color.green);
            } else {
                isOpen = "CLOSED";
                color = ContextCompat.getColor(context,R.color.red);
            }

            hoursString = new SpannableString(isOpen + "\n");
            hoursString.setSpan(new ForegroundColorSpan(color), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("CLOSED ALL DAY\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        Spanned weeklyHoursString = hoursString;
        for (int i=0; i < openings.size(); i++) {
            if (byAppointments.get(i)) {
                hoursString = new SpannableString("\n  BY APPOINTMENT");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.pavan_light) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (openings.get(i) != null && closings.get(i) != null) {
                String opening = HOURS_FORMAT.format(openings.get(i));
                String closing = HOURS_FORMAT.format(closings.get(i));
                hoursString = new SpannableString("\n" + opening + " - " + closing);
            } else {
                hoursString = new SpannableString("\n  CLOSED ALL DAY");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            c.setTime(openings.get(i));
            if (today == c.get(Calendar.DAY_OF_WEEK)) {
                hoursString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, hoursString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }
        return weeklyHoursString;
    }
}
