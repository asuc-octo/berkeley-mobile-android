package com.asuc.asucmobile.main;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Library;

import java.text.SimpleDateFormat;

public class OpenLibraryActivity extends Activity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a");

    public static Library staticLibrary;

    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        }
        setContentView(R.layout.activity_open_library);

        library = staticLibrary;

        if (getActionBar() != null) {
            getActionBar().setTitle(library.getName());
        }

        ImageView availabilityIcon = (ImageView) findViewById(R.id.availability_icon);
        TextView availabilityText = (TextView) findViewById(R.id.availability);
        TextView hoursHeader = (TextView) findViewById(R.id.hours_header);
        TextView locationHeader = (TextView) findViewById(R.id.location_header);
        TextView phoneHeader = (TextView) findViewById(R.id.phone_header);
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);

        availabilityText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        hoursHeader.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        locationHeader.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        phoneHeader.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        hours.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        address.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        phone.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));

        if (library.isByAppointment()) {
            availabilityIcon.setImageDrawable(getResources().getDrawable(R.drawable.question_mark_blue));
            availabilityText.setText("By Appointment");
        } else if (library.isOpen()) {
            availabilityIcon.setImageDrawable(getResources().getDrawable(R.drawable.check_mark_blue));
            availabilityText.setText("Open");
        } else {
            availabilityIcon.setImageDrawable(getResources().getDrawable(R.drawable.no_entry_blue));
            availabilityText.setText("Closed");
        }

        String hoursString;
        if (library.isByAppointment()) {
            hoursString = "By Appointment";
        } else if (library.getOpening() != null && library.getClosing() != null) {
            hoursString =
                    HOURS_FORMAT.format(library.getOpening()) +
                    " to " +
                    HOURS_FORMAT.format(library.getClosing()
            );
        } else {
            hoursString = "Closed All Day";
        }

        hours.setText(hoursString);
        address.setText(library.getLocation());
        phone.setText(library.getPhone());
    }

}
