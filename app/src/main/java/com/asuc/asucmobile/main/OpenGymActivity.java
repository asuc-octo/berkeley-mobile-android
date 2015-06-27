package com.asuc.asucmobile.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.flurry.android.FlurryAgent;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenGymActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PLAY_SERVICES = 1;
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

        ImageView image = (ImageView) findViewById(R.id.image);
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

        new DownloadImageThread(image, gym.getImageUrl()).start();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PLAY_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                        this,
                        "Google Play Services must be installed to display map.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class DownloadImageThread extends Thread {

        ImageView image;
        String url;

        public DownloadImageThread(ImageView image, String url) {
            this.image = image;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                InputStream input = new java.net.URL(url).openStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(input);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            image.setImageBitmap(bitmap);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
