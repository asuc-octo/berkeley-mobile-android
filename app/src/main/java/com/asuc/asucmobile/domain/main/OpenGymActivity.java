package com.asuc.asucmobile.domain.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
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
            new SimpleDateFormat("EEE h:mm a", Locale.ENGLISH);

    private static Gym gym;
    private FirebaseAnalytics mFirebaseAnalytics;


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
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.progress_bar);
        final ImageView image = (ImageView) findViewById(R.id.image);

        Spannable hoursStringSpannable;
        String hoursString;
        if (gym.getOpening() != null && gym.getClosing() != null) {
            String isOpen;
            int color;
            if (gym.isOpen()) {
                isOpen = "OPEN";
                color = Color.rgb(153, 204, 0);
            } else {
                isOpen = "CLOSED";
                color = Color.rgb(255, 68, 68);
            }
            ArrayList<Date> openingTimes = gym.getOpening();
            ArrayList<Date> closingTimes = gym.getClosing();
            String currentOpening;
            String currentClosing;
            hoursString = "Today  " + isOpen + "\n";
            for (int i = 0; i < openingTimes.size(); i++) {
                currentOpening = HOURS_FORMAT.format(openingTimes.get(i));
                currentClosing = HOURS_FORMAT.format(closingTimes.get(i));
                if (i != openingTimes.size() - 1) {
                    currentClosing += "\n";
                }
                hoursString += currentOpening + " to " + currentClosing;
            }
            hoursStringSpannable = new SpannableString(hoursString);
            hoursStringSpannable.setSpan(new ForegroundColorSpan(color), 7, 7 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursStringSpannable = new SpannableString("Today  UNKNOWN");
            hoursStringSpannable.setSpan(new ForegroundColorSpan(Color.rgb(114, 205, 244)), 7, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        hours.setText(hoursStringSpannable);
        address.setText(gym.getAddress());

        // Load gym image.
        Glide.with(this).load(gym.getImageUrl()).into(image);
        loadingBar.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
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