package com.asuc.asucmobile.main;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.RouteAdapter;
import com.asuc.asucmobile.controllers.RouteController;
import com.asuc.asucmobile.models.Route;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.Trip;
import com.asuc.asucmobile.views.MapHeaderView;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nirhart.parallaxscroll.views.ParallaxListView;

public class OpenRouteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PLAY_SERVICES = 1;

    private MapFragment mapFragment;
    private GoogleMap map;
    private Route route;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        route = ((RouteController) RouteController.getInstance(this)).getCurrentRoute();
        if (route == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_open_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ParallaxListView routeList = (ParallaxListView) findViewById(R.id.stop_list);

        routeList.addParallaxedHeaderView(new MapHeaderView(this));
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        setUpMap();

        RouteAdapter adapter = new RouteAdapter(this, route);
        routeList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        FlurryAgent.logEvent("Opened BearTransit Route");

        route = ((RouteController) RouteController.getInstance(this)).getCurrentRoute();
        if (route == null) {
            finish();
            return;
        }

        setUpMap();
    }

    @Override
    public void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    private void setUpMap() {
        // Checking if Google Play Services are available to set up the map.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil
                        .getErrorDialog(status, this, REQUEST_CODE_PLAY_SERVICES).show();
            } else {
                Toast.makeText(
                        this,
                        "Unable to display map. Make sure you have Google Play Services.",
                        Toast.LENGTH_LONG
                ).show();
            }

            return;
        }

        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                Trip firstTrip = route.getTrips().get(0);
                Stop firstStop = firstTrip.getStops().get(0);
                Trip lastTrip = route.getTrips().get(route.getTrips().size() - 1);
                Stop lastStop = lastTrip.getStops().get(lastTrip.getStops().size() - 1);

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                String bestProvider = locationManager.getBestProvider(new Criteria(), false);

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                map.addMarker(new MarkerOptions()
                                .position(lastStop.getLocation())
                                .icon(bitmap)
                );
                map.addMarker(new MarkerOptions()
                        .position(firstStop.getLocation())
                        .icon(bitmap)
                );
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(true);

                final LatLngBounds.Builder builder = LatLngBounds.builder();
                builder.include(firstStop.getLocation());
                builder.include(lastStop.getLocation());

                try {
                    Location myLocation = locationManager.getLastKnownLocation(bestProvider);
                    builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                } catch (SecurityException e) {
                    // Don't do anything
                }

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 155));
                    }
                });
            }
        }
    }

}
