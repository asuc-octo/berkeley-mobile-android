package com.asuc.asucmobile.domain.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.models.Gym;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OpenGymActivity extends BaseActivity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private static Gym gym;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ViewGroup.LayoutParams hoursParams;


    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_gym);
        exitIfNoData();
        setupToolbar(gym.getName(), true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("gym", gym.getName());
        mFirebaseAnalytics.logEvent("opened_gym", bundle);

        // Populate UI.
        TextView address = (TextView) findViewById(R.id.location);
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.progress_bar);
        final ImageView image = (ImageView) findViewById(R.id.image);
        address.setText(gym.getAddress());

        setUpHours();

        // Load gym image.
        Glide.with(this).load(gym.getImageUrl()).into(image);
        loadingBar.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
    }

    /**
     * This code sets up daily hours, only.
     */
    private void setUpHours() {
        final TextView hours = (TextView) findViewById(R.id.hours);
        final TextView hours_expand = (TextView) findViewById(R.id.hours_expand);
        final LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);

        // weekly gym hours should already be set up when you open the page
        hours.setText(setUpWeeklyHoursLeft());
        hoursParams = hoursLayout.getLayoutParams();
        hoursLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
        hours_expand.setText(setUpWeeklyHoursRight());
    }

    /**
     * This function is called with setUpWeeklyHoursRight() to set up the weekly hours
     * page, which essentially appears as left and right justified text. The left side
     * holds days of the week and some description.
     */
    private Spanned setUpWeeklyHoursLeft() {
        ArrayList<Date> openings = gym.getWeeklyOpen();
        Spanned weeklyHoursString = new SpannableString("Today\n");
        for (int i=0; i < openings.size(); i++) {
            Spannable hoursString;
            hoursString = new SpannableString("\n" + gym.getDayOfWeek(i));
            if (i == 0) {
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
    private Spanned setUpWeeklyHoursRight() {
        ArrayList<Date> openings = gym.getWeeklyOpen();
        ArrayList<Date> closings = gym.getWeeklyClose();
        ArrayList<Boolean> byAppointments = gym.getWeeklyAppointments();
        Spannable hoursString;
        if (gym.isByAppointment()) {
            hoursString = new SpannableString("BY APPOINTMENT\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light )), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (openings != null && closings != null) {
            String isOpen;
            int color;
            if (gym.isOpen()) {
                isOpen = "OPEN";
                color = ContextCompat.getColor(getApplicationContext(),R.color.green);
            } else {
                isOpen = "CLOSED";
                color = ContextCompat.getColor(getApplicationContext(),R.color.red);
            }

            hoursString = new SpannableString(isOpen + "\n");
            hoursString.setSpan(new ForegroundColorSpan(color), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("CLOSED ALL DAY\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        Spanned weeklyHoursString = hoursString;
        for (int i=0; i < openings.size(); i++) {
            if (byAppointments.get(i)) {
                hoursString = new SpannableString("\n  BY APPOINTMENT");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (openings.get(i) != null && closings.get(i) != null) {
                String opening = HOURS_FORMAT.format(openings.get(i));
                String closing = HOURS_FORMAT.format(closings.get(i));
                hoursString = new SpannableString("\n" + opening + " - " + closing);
            } else {
                hoursString = new SpannableString("\n  CLOSED ALL DAY");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if (i == 0) {
                hoursString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, hoursString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }
        return weeklyHoursString;
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
    }

    public static void setGym(Gym g) {
        gym = g;
    }

    private void exitIfNoData() {
        if (gym == null) {
            finish();
        }
    }

}