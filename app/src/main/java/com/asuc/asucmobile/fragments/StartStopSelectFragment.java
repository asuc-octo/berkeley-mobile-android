package com.asuc.asucmobile.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.main.LiveBusActivity;
import com.asuc.asucmobile.main.LiveBusActivity.BusCallback;
import com.asuc.asucmobile.main.OpenRouteSelectionActivity;
import com.asuc.asucmobile.main.StopActivity;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.LocationGrabber;
import com.asuc.asucmobile.utilities.NavigationGenerator;
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

public class StartStopSelectFragment extends Fragment
    implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION = 0;
    private static final int START_INT = 1;
    private static final int END_INT = 2;
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("MMM d @ h:mm a", Locale.ENGLISH);

    private static Calendar departureTime = Calendar.getInstance();

    private Context context;
    private View layout;
    private TextView startButton;
    private TextView destButton;
    private LinearLayout refreshWrapper;
    private MapFragment mapFragment;
    private String startName;
    private String endName;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private BusCallback busCallback;
    private Timer timer;
    private GoogleMap map;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            layout = inflater.inflate(R.layout.fragment_start_stop_select, container, false);
        } catch (InflateException e) {
            return layout;
        }
        ImageView menuButton = (ImageView) layout.findViewById(R.id.menu_button);
        NavigationGenerator.generateToolbarMenuButton(menuButton);
        startButton = (TextView) layout.findViewById(R.id.start_stop);
        destButton = (TextView) layout.findViewById(R.id.dest_stop);
        TextView timeButton = (TextView) layout.findViewById(R.id.departure_time);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
        ImageView myLocationButton = (ImageView) layout.findViewById(R.id.my_location);
        FloatingActionButton navigateButton =
                (FloatingActionButton) layout.findViewById(R.id.navigate_button);

        navigateButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation));
        refreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        context = getContext();
        startButton.setOnClickListener(new StartStopListener(START_INT));
        destButton.setOnClickListener(new StartStopListener(END_INT));
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationGrabber.getLocation(StartStopSelectFragment.this,  new LocationCallback());
                startButton.setText(getString(R.string.retrieving_location));
            }
        });
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OpenRouteSelectionActivity.class);
                if (startLatLng == null) {
                    Toast.makeText(context, "Please select a start location",Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (endLatLng == null) {
                    Toast.makeText(context, "Please select an end location", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Should be impossible.
                if (departureTime == null) {
                    Toast.makeText(context, "Please select a departure time", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                intent.putExtra("startLngLat", startLatLng);
                intent.putExtra("endLngLat", endLatLng);
                intent.putExtra("departureTime", departureTime.getTime());
                startActivity(intent);
            }
        });
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        timer = new Timer("liveBus", true);
        LocationGrabber.getLocation(StartStopSelectFragment.this,  new RawLocationCallback());
        NavigationGenerator.closeMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        LiveBusActivity.stopBusTracking();
        stopLocationTracking();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            stopLocationTracking();
        } catch (Exception e) {
            // Don't worry about it!
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
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

    private void stopLocationTracking() {
        try {
            if (map != null &&
                    ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            // Do nothing! Because sometimes the map may get nulled out during the if statement.
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

        private StartStopListener(int typeRequest) {
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
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.871899, -122.25854), 14.5f);
        map.moveCamera(update);
        busCallback = new BusCallback(context, map, refreshWrapper, timer);
        liveTrack();
    }

    private void liveTrack() {
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        BusController.getInstance().refreshInBackground(getActivity(), busCallback);
                    }
                }
            }, 0L, 3000L);
        } catch (Exception e) {
            // Don't worry about it!
        }
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
            Toast.makeText(getActivity(), "Please allow location permissions and try again", Toast.LENGTH_SHORT).show();
            startButton.setText("");
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
            TextView timeButton = (TextView) getActivity().findViewById(R.id.departure_time);
            departureTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            departureTime.set(Calendar.MINUTE, minute);
            timeButton.setText(TIME_FORMAT.format(departureTime.getTime()));
        }

    }

    private class LocationCallback implements Callback {

        @Override
        public void onDataRetrieved(Object data) {
            try {
                startButton.setText(R.string.my_location);
                startName = getResources().getString(R.string.my_location);
                startLatLng = (LatLng) data;
            } catch (Exception e) {
                // In case the fragment is switched out while getting location.
            }
        }

        @Override
        public void onRetrievalFailed() {
            try {
                Toast.makeText(getActivity(), "Unable to find your location", Toast.LENGTH_SHORT)
                        .show();
                startButton.setText("");
            } catch (Exception e) {
                // In case the fragment is switched out while getting location.
            }
        }

    }

    private class RawLocationCallback implements Callback {
        @Override
        public void onDataRetrieved(Object data) {
            try {
                if (data == null) {
                    return;
                }
                startButton.setText(R.string.my_location);
                startName = getResources().getString(R.string.my_location);
                startLatLng = (LatLng) data;
            } catch (Exception e) {
                // In case the fragment is switched out while getting location.
            }
        }

        @Override
        public void onRetrievalFailed() {}

    }

}
