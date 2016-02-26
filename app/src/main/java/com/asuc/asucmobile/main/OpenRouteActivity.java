package com.asuc.asucmobile.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.adapters.RouteAdapter;
import com.asuc.asucmobile.controllers.RouteController;
import com.asuc.asucmobile.models.Category;
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
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.beartransit), "BearTransit", new Intent(this, StartStopSelectActivity.class)),
                new Category(getResources().getDrawable(R.drawable.dining_hall), "Dining Halls", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.library), "Libraries", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.gym), "Gyms", new Intent(this, GymActivity.class))
        };

        final MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this, menuItems);
        mDrawerList.setAdapter(mainMenuAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(mainMenuAdapter.getItem(i).getIntent());
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.live_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.live_bus:
                Intent intent = new Intent(getBaseContext(), LiveBusActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
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

                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_pin);
                BitmapDescriptor pin = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, 39, 64, false));

                map.addMarker(new MarkerOptions()
                                .position(lastStop.getLocation())
                                .icon(pin)
                );
                map.addMarker(new MarkerOptions()
                                .position(firstStop.getLocation())
                                .icon(pin)
                );
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }

                final LatLngBounds.Builder builder = LatLngBounds.builder();
                builder.include(firstStop.getLocation());
                builder.include(lastStop.getLocation());

                try {
                    Location myLocation = locationManager.getLastKnownLocation(bestProvider);
                    if (myLocation != null) {
                        builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                    }
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
