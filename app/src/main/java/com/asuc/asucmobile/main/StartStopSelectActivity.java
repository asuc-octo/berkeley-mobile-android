package com.asuc.asucmobile.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class StartStopSelectActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private static final int START_INT = 1;
    private static final int END_INT = 2;

    private MapFragment mapFragment;
    private StopAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_select);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View startButton = findViewById(R.id.select_start);
        View endButton =  findViewById(R.id.select_dest);
        final Context context = getApplicationContext();
        startButton.setOnClickListener(new StartStopListener(START_INT, context));
        endButton.setOnClickListener(new StartStopListener(END_INT, context));
    }

    private class StartStopListener implements View.OnClickListener {

        private int typeRequest;
        private Context context;

        public StartStopListener(int typeRequest, Context context) {
            this.typeRequest = typeRequest;
        }
        @Override
        public void onClick(View view) {
            startActivityForResult(new Intent(context, ListStartStopActivity.class), typeRequest);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}
