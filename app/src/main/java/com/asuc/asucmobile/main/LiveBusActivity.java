package com.asuc.asucmobile.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.models.Bus;
import com.asuc.asucmobile.utilities.Callback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class LiveBusActivity extends BaseActivity implements OnMapReadyCallback {

    public static Timer timer;

    private LiveBusActivity activity;
    private MapFragment mapFragment;
    private Callback busCallback;
    private LinearLayout mRefreshWrapper;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_live_bus);
        setupToolbar("Live Bus Map", true);
        this.activity = this;

        // Populate UI.
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    liveTrack();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer("liveBus", true);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.872508, -122.260783), 14.5f);
        googleMap.moveCamera(update);
        busCallback = new BusCallback(getBaseContext(), googleMap, mRefreshWrapper, timer);
        liveTrack();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBusTracking();
    }

    private void liveTrack() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BusController.getInstance().refreshInBackground(LiveBusActivity.this, busCallback);
            }
        }, 0L, 3000L);
    }

    public static void stopBusTracking() {
        if (timer != null) {
            try {
                timer.cancel();
                timer.purge();
                timer = null;
            } catch (Exception e) {
                // Don't worry about it!
            }
        }
    }

    public static class BusCallback implements Callback {

        private int failCount = 0;
        private GoogleMap map;
        private LinearLayout refreshWrapper;
        private HashMap<String, Marker> markers;
        private Timer timer;
        private BitmapDescriptor icon;

        public BusCallback(Context context, GoogleMap map, LinearLayout refreshWrapper, Timer timer) {
            this.map = map;
            this.refreshWrapper = refreshWrapper;
            this.markers = new HashMap<>();
            this.timer = timer;
            Bitmap b =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.transit_blue);
            icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, 96, 96, false));
            map.clear();
        }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void onDataRetrieved(Object data) {
            refreshWrapper.setVisibility(View.GONE);
            if (failCount != 0) {
                failCount = 0;
            }
            ArrayList<Bus> buses = (ArrayList<Bus>) data;
            HashSet<String> lines = new HashSet<>();

            // Update bus locations and add new buses if available.
            for (Bus bus : buses) {
                if (bus.getLineName() != null && !bus.getLineName().equals("null")) {
                    lines.add(bus.getLineName());
                    if (!markers.containsKey(bus.getLineName())) {
                        markers.put(
                                bus.getLineName(),
                                map.addMarker(new MarkerOptions()
                                        .position(bus.getLocation())
                                        .icon(icon)
                                        .title(bus.getLineName())
                        ));
                    }
                    markers.get(bus.getLineName()).setPosition(bus.getLocation());
                }
            }

            // Go through current markers and remove buses that did not get updated.
            for (String line : markers.keySet()) {
                if (!lines.contains(line)) {
                    markers.get(line).remove();
                    markers.remove(line);
                }
            }
        }

        @Override
        public synchronized void onRetrievalFailed() {
            failCount++;
            if (failCount > 1) {
                timer.purge();
                map.clear();
                refreshWrapper.setVisibility(View.VISIBLE);
            }
        }

    }

}
