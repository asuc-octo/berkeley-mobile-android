package com.asuc.asucmobile.main;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.models.Bus;
import com.asuc.asucmobile.utilities.Callback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LiveBusActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private Timer timer;
    private LiveBusActivity activity;
    private MapFragment mapFragment;
    private Callback busCallback;
    private LinearLayout mRefreshWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_bus);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveTrack();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.getMapAsync(this);
        timer = new Timer("liveBus", true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMyLocationEnabled(true);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.872508, -122.260783), 14.5f);
        map.moveCamera(update);
        busCallback = new BusCallback(map, mRefreshWrapper, timer, getBaseContext());
        liveTrack();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.purge();
    }

    private void liveTrack() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BusController.getInstance(activity).refreshInBackground(busCallback);
            }
        }, 0L, 3000L);
    }

    public static class BusCallback implements Callback {

        private int failCount = 0;
        private GoogleMap map;
        private LinearLayout refreshWrapper;
        private ArrayList<Bus> buses;
        private Timer timer;
        private Context context;

        public BusCallback(GoogleMap map, LinearLayout refreshWrapper, Timer timer, Context context) {
            this.map = map;
            this.refreshWrapper = refreshWrapper;
            this.timer = timer;
            this.context = context;
        }
        @Override
        public synchronized void onDataRetrieved(Object data) {
            refreshWrapper.setVisibility(View.GONE);
            if (failCount != 0) {
                failCount = 0;
            }
            buses = (ArrayList<Bus>) data;
            map.clear();
            for(Bus bus : buses) {
                map.addMarker(new MarkerOptions().position(bus.getLocation()));
            }
        }

        @Override
        public synchronized void onRetrievalFailed() {
            failCount++;
            if (failCount > 1) {
                timer.purge();
                map.clear();
                refreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
