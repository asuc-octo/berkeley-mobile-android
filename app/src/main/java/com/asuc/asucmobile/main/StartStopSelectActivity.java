package com.asuc.asucmobile.main;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.main.LiveBusActivity.BusCallback;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.LocationGrabber;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class StartStopSelectActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION = 0;
    private static final int START_INT = 1;
    private static final int END_INT = 2;
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("MMM d @ h:mm a", Locale.ENGLISH);

    private Context context;

    private TextView startButton;
    private TextView destButton;
    private static TextView timeButton;
    private LinearLayout refreshWrapper;
    private MapFragment mapFragment;

    private String startName;
    private String endName;

    private LatLng startLatLng;
    private LatLng endLatLng;

    private BusCallback busCallback;
    private Timer timer;
    private StartStopSelectActivity activity;

    private static Calendar departureTime = Calendar.getInstance();

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_select);
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        startButton = (TextView) findViewById(R.id.start_stop);
        destButton = (TextView) findViewById(R.id.dest_stop);
        timeButton = (TextView) findViewById(R.id.departure_time);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        ImageView myLocationButton = (ImageView) findViewById(R.id.my_location);
        FloatingActionButton navigateButton = (FloatingActionButton) findViewById(R.id.navigate_button);

        navigateButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation));
        refreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        context = getBaseContext();
        startButton.setOnClickListener(new StartStopListener(START_INT));
        destButton.setOnClickListener(new StartStopListener(END_INT));

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationGrabber.getLocation(StartStopSelectActivity.this,  new LocationCallback());
                startButton.setText(getString(R.string.retrieving_location));
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);
                if (startLatLng == null) {
                    Toast.makeText(context, "Please select a start location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endLatLng == null) {
                    Toast.makeText(context, "Please select an end location", Toast.LENGTH_SHORT).show();
                    return;
                }
                //should be impossible
                if (departureTime == null) {
                    Toast.makeText(context, "Please select a departure time", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("startLngLat", startLatLng);
                intent.putExtra("endLngLat", endLatLng);
                intent.putExtra("departureTime", departureTime.getTime());
                startActivity(intent);
            }
        });
        this.activity = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        timer = new Timer("liveBus", true);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == START_INT) {
            getStartFromPref(data);
            startButton.setText(startName);
        } else if (requestCode == END_INT) {
            getEndFromPref(data);
            destButton.setText(endName);
        }
    }

    private void getStartFromPref(Intent data) {
        startName = data.getStringExtra("startName");
        startLatLng = data.getParcelableExtra("startLatLng");
    }

    private void getEndFromPref(Intent data) {
        endName = data.getStringExtra("endName");
        endLatLng = data.getParcelableExtra("endLatLng");
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
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.871899, -122.25854), 14.5f);
        map.moveCamera(update);
        busCallback = new BusCallback(map, refreshWrapper, timer, context);
        liveTrack();
    }

    private void liveTrack() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BusController.getInstance(activity).refreshInBackground(busCallback);
            }
        }, 0L, 3000L);
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
        @NonNull
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
            timeButton.setText(TIME_FORMAT.format(departureTime.getTime()));
        }
    }

    private class LocationCallback implements Callback {

        @Override
        public void onDataRetrieved(Object data) {
            startButton.setText(R.string.my_location);
            startName = getResources().getString(R.string.my_location);
            startLatLng = (LatLng) data;
        }

        @Override
        public void onRetrievalFailed() {
            Toast.makeText(StartStopSelectActivity.this, "Unable to find your location", Toast.LENGTH_SHORT).show();
        }

    }

}
