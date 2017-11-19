package com.asuc.asucmobile.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.LiveBusController;
import com.asuc.asucmobile.controllers.RouteController;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.main.LiveBusActivity;
import com.asuc.asucmobile.main.PopUpActivity;
import com.asuc.asucmobile.main.RouteSelectActivity;
import com.asuc.asucmobile.models.Buses;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;
import com.asuc.asucmobile.utilities.LocationGrabber;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mbanje.kurt.fabbutton.FabButton;
import retrofit2.Call;


public class MapsFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLoadedCallback {

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    Gson gson = new Gson();
    final LatLng BERKELEY = new LatLng(37.8716, -122.2727);
    private GoogleApiClient googleApiClient;
    double longitude;
    double latitude;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    LatLng currLocation;
    View mapView;
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private Button busesNearby;
    FabButton navigation_button;
    final int color = Color.rgb(38, 38, 206); //Default color theme
    final int gray = Color.rgb(168, 168, 168); //Default unselected color
    ArrayList<Marker> markers_ACTransit = new ArrayList<>();
    HashMap<Marker, String> marker_to_ID = new HashMap<>(); //Given marker, gets ID (we can do this because marker is FINAL
    boolean bearTransitPressed;
    LinearLayout originWrapper;
    RelativeLayout busRouteWrapper;
    private LinearLayout refreshWrapper;

    private final double distThresh = 0.25;
    private boolean nearByHighlighted = false;
    @SuppressWarnings("all")
    private static View layout;
    private MapFragment mapFragment;
    private LiveBusActivity.BusCallback busCallback;


    private static MapsFragment instance;
    private static Marker prevMarker;


    public static MapsFragment getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings("all")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
        }
        try {
            layout = inflater.inflate(R.layout.activity_maps, container, false);
        } catch (Exception e) {
            Log.e("hi", e.toString());


        }
//            Log.e("hi", e.getStackTrace().toString());
        // Don't worry about it!

        instance = this;

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Bear Transit");
        setHasOptionsMenu(true);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        refreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);


        final DestinationFragment searchBar = (DestinationFragment) getActivity().getFragmentManager().findFragmentById(R.id.destination_bar);
        final OriginFragment originBar = (OriginFragment) getActivity().getFragmentManager().findFragmentById(R.id.origin_bar);
        navigation_button = (FabButton) layout.findViewById(R.id.determinate);
        busesNearby = (Button) layout.findViewById(R.id.busesNearby);

        /*busesNearby.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                try {
                    mMap.setMyLocationEnabled(true);
                    Log.e("Location Enabled", "location");
                    LocationManager lm = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    currLocation = new LatLng(latitude, longitude);
                } catch (Exception e) {
                    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    currLocation = BERKELEY;
                }


                if (!nearByHighlighted) {
                    highlightButton(busesNearby, !nearByHighlighted);

                    for (Marker marker : markers_ACTransit) {
                        double dist = Double.valueOf(getDistance(marker.getPosition().latitude, marker.getPosition().longitude));
                        if (dist < distThresh) {
                            marker.setVisible(true);
                        }
                    }
                    nearByHighlighted = !nearByHighlighted;

                } else {
                    highlightButton(busesNearby, !nearByHighlighted);
                    for (Marker marker : markers_ACTransit) {
                        marker.setVisible(false);
                    }
                    nearByHighlighted = !nearByHighlighted;
                }
            }


        });*/


        navigation_button.setOnClickListener(new View.OnClickListener()

        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LatLng destination = searchBar.getDestination();
                LatLng origin = originBar.getOrigin();
                if (destination == null || origin == null) {
                    Toast.makeText(getActivity().getBaseContext(), "Please select a destination and an origin.", Toast.LENGTH_SHORT).show();
                } else {
                    refresh(origin, destination, System.currentTimeMillis());
                }


            }
        });


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        return layout;
    }

    private void liveTrack() {
        LiveBusController.cService controller = Controller.retrofit.create(LiveBusController.cService.class);
        final Call<Buses> call = controller.getData();
        try {
            LiveBusActivity.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    call.clone().enqueue(busCallback);
                }
            }, 0L, 3000L);
        } catch (Exception e) {
            // Don't worry about it!
            System.out.println(e);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        //Zooms camera to "my location"
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        try {
            mMap.setMyLocationEnabled(true);
            Log.e("Location Enabled", "location");
            LocationManager lm = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            currLocation = new LatLng(latitude, longitude);
        } catch (Exception e) {
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


            currLocation = BERKELEY;
        }
        LiveBusActivity.timer = new Timer("liveBus", true);
        busCallback = new LiveBusActivity.BusCallback(getContext(), mMap, refreshWrapper, LiveBusActivity.timer);


        //Clears focus when user clicks on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (originWrapper == null || busRouteWrapper == null || navigation_button == null) {
                    originWrapper = (LinearLayout) layout.findViewById(R.id.origin_bar_wrapper);
                    navigation_button = (FabButton) layout.findViewById(R.id.determinate);

                }
                navigation_button.setVisibility(View.VISIBLE);
                hideKeyboard(mapView);
                clearFocus();
            }
        });

        //Changes the FAB (my location button) to bottom right
        moveNavigationIcon(mapView);
        cameraLoadAnimation(currLocation);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(this);

        Type listType = new TypeToken<HashMap<String, Stop>>() {
        }.getType();


        //  HashMap<String, Stop> AC_plots = gson.fromJson(JSONUtilities.readJSONFromAsset(getActivity(), "BerkeleyStops.json"), listType);

        //loadMarkers(AC_plots);


        liveTrack();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void markerZoom(final Marker marker) {
        clearFocus();
        if ((boolean) marker.getTag()) {
            highlightButton(this.busesNearby, true);

        } else {
            highlightButton(this.busesNearby, false);
        }
        cameraLoadAnimation(marker.getPosition());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onMarkerClick(final Marker marker) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markerclicked);

        if ((boolean) marker.getTag()) {
            return true;
        }


        if (this.prevMarker != null) {
            this.prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markernew));
            this.prevMarker = marker;
        }

        this.prevMarker = marker;
        Intent popUp;
        clearFocus();
        marker.setIcon(icon);
        popUp = new Intent(getActivity(), PopUpActivity.class);
        popUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        popUp.putExtra("title", marker.getTitle());
        popUp.putExtra("distance", getDistance(marker.getPosition().latitude, marker.getPosition().longitude));
        popUp.putExtra("id", marker_to_ID.get(marker));
        popUp.putExtra("location", marker.getPosition());

        startActivity(popUp);
        return true;
    }

    public void cameraLoadAnimation(LatLng location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void highlightButton(Button button, boolean highlight) {
        if (highlight) {
            button.setTextColor(Color.WHITE);
            button.setBackground(ContextCompat.getDrawable(mapView.getContext(), R.drawable.ac_bear_transit_pop_up_window_button_pressed));
        } else {
            button.setTextColor(Color.BLUE);
            button.setBackground(ContextCompat.getDrawable(mapView.getContext(), R.drawable.ac_bear_transit_pop_up_window_button_shape));

        }
    }

    @Override
    public void onMapLoaded() {

        // originWrapper = (LinearLayout) layout.findViewById(R.id.origin_bar_wrapper);
        //bearTransitPressed = false;

    }


    public void clearFocus() {
        View current = getActivity().getCurrentFocus();
        if (current != null) {
            current.clearFocus();
        }
    }


    private void loadMarkers(HashMap<String, Stop> AC_plots) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markernew);
        if (markers_ACTransit.size() == 0) {
            for (String id : AC_plots.keySet()) {
                double lat = AC_plots.get(id).getLatitude();
                double lon = AC_plots.get(id).getLongitude();
                String title = AC_plots.get(id).getName();
                MarkerOptions singleMarkerOption = new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .icon(icon)
                        .visible(false)
                        .title(title);
                Marker singleMarker = mMap.addMarker(singleMarkerOption);
                singleMarker.setTag(false);
                marker_to_ID.put(singleMarker, id);
                markers_ACTransit.add(singleMarker);
            }
        } else {
            return;
        }


    }

    public String getDistance(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(lat, lng, currLocation.latitude, currLocation.longitude, results);
        return DECIMAL_FORMAT.format(results[0] * 0.000621371192);
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void moveNavigationIcon(View mapView) {
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        //LocationGrabber.getLocation(MapsFragment.this,  new RawLocationCallback());
        NavigationGenerator.closeMenu(getActivity());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.live_bus:
                Intent intent = new Intent(getContext(), LiveBusActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        LiveBusActivity.stopBusTracking();
        stopLocationTracking();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment mapToDestroy = getFragmentManager().findFragmentById(R.id.map);
        Fragment originFragment = getFragmentManager().findFragmentById(R.id.origin_bar);
        Fragment destFragment = getFragmentManager().findFragmentById(R.id.destination_bar);

        if (mapToDestroy != null) {
            getFragmentManager().beginTransaction().remove(mapToDestroy).commit();
            getFragmentManager().beginTransaction().remove(originFragment).commit();
            getFragmentManager().beginTransaction().remove(destFragment).commit();

        }
        try {
            stopLocationTracking();
        } catch (Exception e) {
            // Don't worry about it!
        }
    }

    private void stopLocationTracking() {
        try {
            if (mMap != null &&
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            // Do nothing! Because sometimes the map may get nulled out during the if statement.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refresh(final LatLng origin, final LatLng destination, final Long millis) {
        navigation_button.showProgress(true);

        RouteController.createInstance(origin, destination, millis);

        LineController.getInstance().refreshInBackground(getActivity(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {

                RouteController.getInstance().refreshInBackground(getActivity(), new Callback() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataRetrieved(Object data) {
                        navigation_button.showProgress(false);

                        if (((ArrayList) data).size() == 0) {
                            onRetrievalFailed();

                        } else {
                            Intent routeSelect = new Intent(getContext(), RouteSelectActivity.class);
                            ArrayList<Journey> routes = (ArrayList<Journey>) data;
                            routeSelect.putExtra("routes", routes);
                            startActivity(routeSelect);
                        }


                    }

                    @Override
                    public void onRetrievalFailed() {
                        navigation_button.showProgress(false);
                        Toast.makeText(getActivity().getBaseContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRetrievalFailed() {

            }
        });
        {
        }

    }

    public LatLng getCurrLocation() {
        return currLocation;
    }


}