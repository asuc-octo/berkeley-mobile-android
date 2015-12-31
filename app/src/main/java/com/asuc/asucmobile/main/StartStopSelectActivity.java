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
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.models.Stop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

//TODO: flurry
//TODO: check for existing location through sharedPreferences
public class StartStopSelectActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private static final int START_INT = 1;
    private static final int END_INT = 2;

    private Context context;

    private MapFragment mapFragment;
    private Button startButton;
    private Button endButton;

    private String startName;
    private String endName;

    private LatLng startLatLng;
    private LatLng endLatLng;

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

        context = getBaseContext();
        startButton.setOnClickListener(new StartStopListener(START_INT));
        endButton.setOnClickListener(new StartStopListener(END_INT));

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);
                if(startLatLng == null || endLatLng == null) {
                    Toast.makeText(getBaseContext(), "Please select a start and an end location", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("startLngLat", startLatLng);
                intent.putExtra("endLngLat", endLatLng);
//                intent.putExtra("stop_id", mAdapter.getItem(i).getId());
//                intent.putExtra("stop_name", mAdapter.getItem(i).getName());
//                intent.putExtra("lat", mLatitude);
//                intent.putExtra("long", mLongitude);

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
//            startButton.setText(startStop.getName());
            startButton.setText(startName);
        } else if (requestCode == END_INT) {
            getEndFromPref(data);
//            endButton.setText(endStop.getName());
            endButton.setText(endName);
        }
//        int[] latLong = data.getIntArrayExtra("latLong");
//        String locName = data.getStringExtra("name");

    }

    private void getStartFromPref(Intent data) {
        startName = data.getStringExtra("startName");
        startLatLng = data.getParcelableExtra("startLatLng");
        //TODO: error checking
//        startStop = ((LineController) (LineController.getInstance(this))).getStop(startId, startName);
    }

    private void getEndFromPref(Intent data) {
        endName = data.getStringExtra("endName");
        endLatLng = data.getParcelableExtra("endLatLng");
        //TODO: error checking
//        endStop = ((LineController) (LineController.getInstance(this))).getStop(endId, endName);
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
}
