package com.asuc.asucmobile.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenGymActivity extends BaseActivity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Gym gym;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_gym);
        exitIfNoData();
        setupToolbar(gym.getName(), true);

        // Populate UI.
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        Spannable hoursString;
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
            String opening = HOURS_FORMAT.format(gym.getOpening());
            String closing = HOURS_FORMAT.format(gym.getClosing());
            hoursString = new SpannableString("Today  " + isOpen + "\n" + opening + " to " + closing);
            hoursString.setSpan(new ForegroundColorSpan(color), 7, 7 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("Today  UNKNOWN");
            hoursString.setSpan(new ForegroundColorSpan(Color.rgb(114, 205, 244)), 7, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        hours.setText(hoursString);
        address.setText(gym.getAddress());
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
    }

    private void exitIfNoData() {
        gym = ((GymController) GymController.getInstance(this)).getCurrentGym();
        if (gym == null) {
            finish();
        }
    }

}
