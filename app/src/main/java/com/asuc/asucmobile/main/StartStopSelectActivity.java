package com.asuc.asucmobile.main;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.LocationGrabber;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

//TODO: flurry
//TODO: check for existing location through sharedPreferences
public class StartStopSelectActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION = 0;
    private static final int START_INT = 1;
    private static final int END_INT = 2;

    private Context context;

    private TextView startButtonLabel;
    private TextView destButtonLabel;
    private static TextView timeButtonLabel;

    private String startName;
    private String endName;

    private LatLng startLatLng;
    private LatLng endLatLng;

    private static Calendar departureTime = Calendar.getInstance();

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout startButton = (LinearLayout) findViewById(R.id.select_start);
        LinearLayout destButton = (LinearLayout) findViewById(R.id.select_dest);
        LinearLayout timeButton = (LinearLayout) findViewById(R.id.select_time);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        ImageButton myLocationButton = (ImageButton) findViewById(R.id.my_location);
        startButtonLabel = (TextView) findViewById(R.id.start_stop);
        destButtonLabel = (TextView) findViewById(R.id.dest_stop);
        timeButtonLabel = (TextView) findViewById(R.id.departure_time);
        FloatingActionButton navigateButton = (FloatingActionButton) findViewById(R.id.navigate_button);

        navigateButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation));

        context = getBaseContext();
        startButton.setOnClickListener(new StartStopListener(START_INT));
        destButton.setOnClickListener(new StartStopListener(END_INT));

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationGrabber.getLocation(StartStopSelectActivity.this,  new LocationCallback());
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);
                if (startLatLng == null) {
                    Toast.makeText(getBaseContext(), "Please select a start location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endLatLng == null) {
                    Toast.makeText(getBaseContext(), "Please select an end location", Toast.LENGTH_SHORT).show();
                    return;
                }
                //should be impossible
                if (departureTime == null) {
                    Toast.makeText(getBaseContext(), "Please select a departure Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("startLngLat", startLatLng);
                intent.putExtra("endLngLat", endLatLng);
                intent.putExtra("departureTime", departureTime.getTime());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == START_INT) {
            getStartFromPref(data);
            startButtonLabel.setText(startName);
        } else if (requestCode == END_INT) {
            getEndFromPref(data);
            destButtonLabel.setText(endName);
        }
    }

    private void getStartFromPref(Intent data) {
        startName = data.getStringExtra("startName");
        startLatLng = data.getParcelableExtra("startLatLng");
        //TODO: error checking
    }

    private void getEndFromPref(Intent data) {
        endName = data.getStringExtra("endName");
        endLatLng = data.getParcelableExtra("endLatLng");
        //TODO: error checking
    }

    private class StartStopListener implements View.OnClickListener {

        private int typeRequest;

        public StartStopListener(int typeRequest) {
            this.typeRequest = typeRequest;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, StopActivity.class);
            intent.putExtra("requestCode", typeRequest);
            startActivityForResult(intent, typeRequest);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    /**
     * onRequestPermissionsResult() is called from LocationManager when it requests location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION && grantResults.length > 0 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            LocationGrabber.getLocation(this, new LocationCallback());
        } else {
            Toast.makeText(this, "Please allow location permissions and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = departureTime.get(Calendar.HOUR_OF_DAY);
            int minute = departureTime.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            departureTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            departureTime.set(Calendar.MINUTE, minute);
            timeButtonLabel.setText(departureTime.getTime().toString());
        }
    }

    private class LocationCallback implements Callback {

        @Override
        public void onDataRetrieved(Object data) {
            startButtonLabel.setText(R.string.my_location);
            startName = getResources().getString(R.string.my_location);
            startLatLng = (LatLng) data;
        }

        @Override
        public void onRetrievalFailed() {
            Toast.makeText(StartStopSelectActivity.this, "Unable to find your location", Toast.LENGTH_SHORT).show();
        }

    }

}
