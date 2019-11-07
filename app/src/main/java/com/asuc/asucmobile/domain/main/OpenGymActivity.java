package com.asuc.asucmobile.domain.main;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.utilities.HoursStringGenerator;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenGymActivity extends BaseActivity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("EEEE", Locale.ENGLISH);

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
//        TextView hours = (TextView) findViewById(R.id.hours);
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
        hours.setText(HoursStringGenerator.setUpWeeklyHoursLeft(gym));
        hoursParams = hoursLayout.getLayoutParams();
        hoursLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
        hours_expand.setText(HoursStringGenerator.setUpWeeklyHoursRight(gym, getApplicationContext()));
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