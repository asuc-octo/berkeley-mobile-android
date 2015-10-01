package com.asuc.asucmobile.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.models.Stop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

//TODO: flurry
public class StartStopSelectActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private static final int START_INT = 1;
    private static final int END_INT = 2;

    private MapFragment mapFragment;
    private Button startButton;
    private Button endButton;

    private Stop startStop;
    private Stop endStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_select);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startButton = (Button) findViewById(R.id.select_start);
        endButton =  (Button) findViewById(R.id.select_dest);
        FloatingActionButton navigateButton = (FloatingActionButton) findViewById(R.id.navigate_button);

        navigateButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation, null));

        final Context context = getApplicationContext();
        startButton.setOnClickListener(new StartStopListener(START_INT, context));
        endButton.setOnClickListener(new StartStopListener(END_INT, context));

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);

                intent.putExtra("stop_id", mAdapter.getItem(i).getId());
                intent.putExtra("stop_name", mAdapter.getItem(i).getName());
                intent.putExtra("lat", mLatitude);
                intent.putExtra("long", mLongitude);

                startActivity(intent);
            }
        });3
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        SharedPreferences pref = getSharedPreferences("startStopSelect", MODE_PRIVATE);
        if (requestCode == START_INT) {
            getStartFromPref();
            startButton.setText(startStop.getName());
        } else if (requestCode == END_INT) {
            getEndFromPref();
            endButton.setText(endStop.getName());
        }
//        int[] latLong = data.getIntArrayExtra("latLong");
//        String locName = data.getStringExtra("name");

    }

    private void getStartFromPref() {
        SharedPreferences pref = getSharedPreferences("startStopSelect", MODE_PRIVATE);
        Integer startId = pref.getInt("start_id", -1);
        String startName = pref.getString("start_name", "");
        //TODO: error checking
        startStop = ((LineController) (LineController.getInstance(this))).getStop(startId, startName);
    }

    private void getEndFromPref() {
        SharedPreferences pref = getSharedPreferences("startStopSelect", MODE_PRIVATE);
        Integer endId = pref.getInt("end_id", -1);
        String endName = pref.getString("end_name", "");
        //TODO: error checking
        endStop = ((LineController) (LineController.getInstance(this))).getStop(endId, endName);
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
