package com.asuc.asucmobile.main;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.ImageDownloadThread;
import com.flurry.android.FlurryAgent;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenGymActivity extends AppCompatActivity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private Gym gym;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        gym = ((GymController) GymController.getInstance(this)).getCurrentGym();
        if (gym == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_open_gym);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gym.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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


        //density stuff
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.progress_bar);
        final ProgressBar backgroundBar = (ProgressBar) findViewById(R.id.backing_ring);
        final ProgressBar  percentFullBar = (ProgressBar) findViewById(R.id.percent_full_bar);
        final View image = findViewById(R.id.image);
        final View tintOverlay = findViewById(R.id.tint);
        final TextView percentageText = (TextView) findViewById(R.id.percentage);
        final Integer percentFull = gym.getPercentFull();
        loadingBar.bringToFront();
        new ImageDownloadThread(this, gym.getImageUrl(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                loadingBar.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data;
                Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                image.setBackgroundDrawable(bitmapDrawable);
                image.setVisibility(View.VISIBLE);

                if (percentFull != null) {
                    //gym with density + pic
                    ((GradientDrawable) backgroundBar.getProgressDrawable()).setColor(getResources().getColor(R.color.primary_material_light));
                    backgroundBar.setVisibility(View.VISIBLE);
                    tintOverlay.setVisibility(View.VISIBLE);
                    percentFullBar.setVisibility(View.VISIBLE);
                    percentageText.setVisibility(View.VISIBLE);

                    ObjectAnimator animation = ObjectAnimator.ofInt(percentFullBar,
                            "progress", 0, gym.getPercentFull());
                    animation.setDuration(1000); //in milliseconds
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();

                    percentageText.setText(gym.getPercentFull() + "%");
                } else {
                    //gym with pic
                    backgroundBar.setBackgroundDrawable(bitmapDrawable);
                    backgroundBar.setProgressDrawable(new ColorDrawable(Color.TRANSPARENT));
                    backgroundBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRetrievalFailed() {
                loadingBar.setVisibility(View.GONE);
                if (percentFull != null) {
                    //gym with density
                    ObjectAnimator animation = ObjectAnimator.ofInt(percentFullBar,
                            "progress", 0, gym.getPercentFull());
                    animation.setDuration(1000); //in milliseconds
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();
                } else {
                    //gym with nothing
                    backgroundBar.setVisibility(View.GONE);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        gym = ((GymController) GymController.getInstance(this)).getCurrentGym();
        if (gym == null) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
