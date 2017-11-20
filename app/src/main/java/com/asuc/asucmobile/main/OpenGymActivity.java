package com.asuc.asucmobile.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.ImageDownloadThread;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenGymActivity extends BaseActivity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Gym gym;
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
        final View image = findViewById(R.id.image);
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

        // Load gym image.
        loadingBar.bringToFront();
        new ImageDownloadThread(this, gym.getImageUrl(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data;
                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    image.setBackgroundDrawable(bitmapDrawable);
                }
                loadingBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRetrievalFailed() {
                image.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_gym));
                loadingBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
    }

    private void exitIfNoData() {
        gym = ((GymController) GymController.getInstance()).getCurrentGym();
        if (gym == null) {
            finish();
        }
    }

}
