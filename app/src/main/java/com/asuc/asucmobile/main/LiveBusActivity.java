package com.asuc.asucmobile.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.models.Bus;
import com.asuc.asucmobile.utilities.Callback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LiveBusActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private Timer timer;
    private ArrayList<Bus> buses;
    private LiveBusActivity activity;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_bus);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
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
        liveTrack();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void liveTrack() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BusController.getInstance(activity).refreshInBackground(new Callback() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        buses = (ArrayList<Bus>) data;
                        map.clear();
                        for(Bus bus : buses) {
                            map.addMarker(new MarkerOptions().position(bus.getLocation()));
                        }
                    }

                    @Override
                    public void onRetrievalFailed() {

                    }
                });
            }
        }, 0L, 3000L);
    }


}
