package com.asuc.asucmobile.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.models.Library;

import java.text.SimpleDateFormat;

public class OpenLibraryActivity extends Activity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a");

    public static Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_library);
        if (getActionBar() != null) {
            getActionBar().setTitle(library.getName());
        }

        ImageView availabilityIcon = (ImageView) findViewById(R.id.availability_icon);
        TextView availabilityText = (TextView) findViewById(R.id.availability);
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);

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
        if (library.getOpening() != null && library.getClosing() != null) {
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
