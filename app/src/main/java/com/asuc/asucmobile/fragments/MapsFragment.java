package com.asuc.asucmobile.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.LiveBusController;
import com.asuc.asucmobile.main.LiveBusActivity;
import com.asuc.asucmobile.main.PopUpActivity;
import com.asuc.asucmobile.main.RouteSelectActivity;
import com.asuc.asucmobile.models.Buses;
import com.asuc.asucmobile.models.CategoryLoc;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Line;
import com.asuc.asucmobile.models.LineRespModel;
import com.asuc.asucmobile.models.StopBeforeTransform;
import com.asuc.asucmobile.models.TripBeforeTransform;
import com.asuc.asucmobile.models.TripRespModel;
import com.asuc.asucmobile.models.responses.LineResponse;
import com.asuc.asucmobile.models.responses.MapIconResponse;
import com.asuc.asucmobile.models.responses.TripResponse;
import com.asuc.asucmobile.models.transformers.StopListToLineTransformer;
import com.asuc.asucmobile.models.transformers.TripListToJourneyTransformer;
import com.asuc.asucmobile.services.BMService;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.RoundedBackgroundSpan;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import mbanje.kurt.fabbutton.FabButton;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


public class MapsFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLoadedCallback {

    private static final String TAG = "MapsFragment";

    @Inject BMService bmService;

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
    FabButton navigation_button;
    FloatingActionMenu FABmenu;

    private boolean bottlesShown = false;
    private boolean sleepShown = false;
    private boolean microwavesShown = false;

    public boolean isBottlesShown() {
        return bottlesShown;
    }

    public boolean isSleepShown() {
        return sleepShown;
    }

    public boolean isMicrowavesShown() {
        return microwavesShown;
    }

    ArrayList<Marker> markers_sleepPods = new ArrayList<>();
    ArrayList<Marker> markers_waterbottles = new ArrayList<>();
    ArrayList<Marker> markers_microwave = new ArrayList<>();
    HashMap<Marker, CategoryLoc> markers_to_desc = new HashMap<>();

    HashMap<Marker, String> marker_to_ID = new HashMap<>(); //Given marker, gets ID (we can do this because marker is FINAL
    LinearLayout originWrapper;
    RelativeLayout busRouteWrapper;
    private LinearLayout refreshWrapper;

    @SuppressWarnings("all")
    private static View layout;
    private MapFragment mapFragment;
    private LiveBusActivity.BusCallback busCallback;
    private static MapsFragment instance;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FloatingActionButton microwave, sleepPod, waterBottle;
    private HashMap mapHash = new HashMap<String, ArrayList<CategoryLoc>>();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private boolean showSpotlight = false;

    // Remote Config keys
    private static final String SHOW_SPOTLIGHT = "show_spotlight";

    // Shared Preferences key to track user's first time
    private static final String VIEWED_FIRST_SESSION = "first_session";


    public static MapsFragment getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings("all")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bmService = GlobalApplication.getRetrofitComponent().bmapi();

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_transit_screen", bundle);


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
        FABmenu = (FloatingActionMenu) layout.findViewById(R.id.FABmenu);

        sleepPod = (FloatingActionButton) layout.findViewById(R.id.sleeppod);
        waterBottle = (FloatingActionButton) layout.findViewById(R.id.waterbottle);
        microwave = (FloatingActionButton) layout.findViewById(R.id.microwave);

        // get Remote Config values for spotlight
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        showSpotlight = mFirebaseRemoteConfig.getBoolean(SHOW_SPOTLIGHT);

        if (showSpotlight && !viewedFirstSession()) {
            setSpotlight(FABmenu.getMenuIconView());
        }
        // register first session regardless of if the user viewed spotlight or not

        registerSession();

        FABmenu.setIconAnimated(false);
        FABmenu.setClosedOnTouchOutside(false);

        FABmenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent("view_map_icons_clicked", bundle);

                // Firebase AB test tracking, log clicks after showing spotlight once
                if (viewedFirstSession()) {
                    mFirebaseAnalytics.logEvent("view_map_icons_clicked_after_first_session", bundle);
                }

                if(FABmenu.isOpened()){
                    FABmenu.close(true);
                    FABmenu.setMenuButtonColorNormalResId(R.color.white);
                    FABmenu.getMenuIconView().setImageResource(R.drawable.itemsicons);
                }
                else{
                    FABmenu.open(true);
                    FABmenu.setMenuButtonColorNormalResId(R.color.dark_blue);
                    FABmenu.getMenuIconView().setImageResource(R.drawable.items_icon_pressed);
                }
            }
        });

        sleepPod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                Bundle bundle = new Bundle();
                bundle.putString("Category", "nappod");
                mFirebaseAnalytics.logEvent("map_icon_clicked", bundle);
                updateLocation(v);

                if (viewedFirstSession()) {
                    mFirebaseAnalytics.logEvent("map_icon_clicked_after_first_session", bundle);
                }

                for (Marker marker : markers_sleepPods) {
                    marker.setVisible(!sleepShown);
                }
                sleepShown = !sleepShown;

                for (Marker marker : markers_waterbottles) {
                    marker.setVisible(false);
                }
                bottlesShown = false;

                for (Marker marker : markers_microwave) {
                    marker.setVisible(false);
                }
                microwavesShown = false;
            }
        });

        waterBottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                Bundle bundle = new Bundle();
                bundle.putString("Category", "waterbottles");
                mFirebaseAnalytics.logEvent("map_icon_clicked", bundle);
                updateLocation(v);

                if (viewedFirstSession()) {
                    mFirebaseAnalytics.logEvent("map_icon_clicked_after_first_session", bundle);
                }

                for (Marker marker : markers_waterbottles) {
                    marker.setVisible(!bottlesShown);
                }
                bottlesShown = !bottlesShown;

                for (Marker marker : markers_sleepPods) {
                    marker.setVisible(false);
                }
                sleepShown = false;


                for (Marker marker : markers_microwave) {
                    marker.setVisible(false);
                }
                microwavesShown = false;


            }
        });

        microwave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                Bundle bundle = new Bundle();
                bundle.putString("Category", "microwaves");
                mFirebaseAnalytics.logEvent("map_icon_clicked", bundle);

                if (viewedFirstSession()) {
                    mFirebaseAnalytics.logEvent("map_icon_clicked_after_first_session", bundle);
                }

                //updateLocation(v);
                for (Marker marker : markers_waterbottles) {
                    marker.setVisible(false);
                }
                bottlesShown = false;

                for (Marker marker : markers_sleepPods) {
                    marker.setVisible(false);
                }
                sleepShown = false;

                for (Marker marker : markers_microwave) {
                    marker.setVisible(!microwavesShown);
                }
                microwavesShown = !microwavesShown;


            }
        });


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
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                    Bundle bundle = new Bundle();
                    mFirebaseAnalytics.logEvent("clicked_go_button", bundle);

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

    /**
     * Check the SharedPreferences manager for the VIEWED_FIRST_SESSION key
     * @return whether or not the user viewed the first session or not
     */
    private boolean viewedFirstSession() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getBoolean(MapsFragment.VIEWED_FIRST_SESSION, false);
    }

    /**
     *  Register that the user has completed the first session in the VIEWED_FIRST_SESSION key
     */
    public void registerSession() {
        // Save viewed spotlight value
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        sharedPreferencesEditor.putBoolean(VIEWED_FIRST_SESSION, true);
        sharedPreferencesEditor.apply();
    }

    /**
     *  Show the spotlight on a target View
     * @param target the View that the Spotlight will highlight
     */
    private boolean viewedSpotlight() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getBoolean(MapsFragment.VIEWED_SPOTLIGHT, false);
    }

    private void setSpotlight(View target) {
        String title = "Click the eye to find:";
        String contentText = "• Water filling stations\n• Nap pods\n• Microwaves";

        // do not delete this placeholder span, it does nothing but everything breaks without it
        SpannableString placeholder = new SpannableString(" ");
        SpannableString span = new SpannableString(" GOT IT ");

        span.setSpan(new RoundedBackgroundSpan(Color.argb(255, 220, 220, 220), Color.BLACK, 10,
                20, 50), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(placeholder);
        builder.append(span);

        MaterialShowcaseView.Builder spotlight = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(target)
                .setDismissText(builder)
                .setTitleText(title)
                .setContentText(contentText)
                .setDismissOnTouch(true)
                .setShapePadding(100)
                .setDelay(500);

        spotlight.show();
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
        //Zooms camera to "my locationresizeMapIcons"
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

        if (LiveBusActivity.BusCallback.getInstance() == null) {
            busCallback = new LiveBusActivity.BusCallback(getContext(), mMap, refreshWrapper, LiveBusActivity.timer);
            cameraLoadAnimation(currLocation);
        }

        //Clears focus when user clicks on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (originWrapper == null || busRouteWrapper == null || navigation_button == null) {
                    originWrapper = (LinearLayout) layout.findViewById(R.id.origin_bar);
                    navigation_button = (FabButton) layout.findViewById(R.id.determinate);

                }
                navigation_button.setVisibility(View.VISIBLE);
                hideKeyboard(mapView);
                clearFocus();
            }
        });

        //Changes the FAB (my location button) to bottom right
        moveNavigationIcon(mapView);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(this);

        bmService.callIconList().enqueue(new retrofit2.Callback<MapIconResponse>() {
            @Override
            public void onResponse(Call<MapIconResponse> call, Response<MapIconResponse> response) {
                mapHash.put("Microwave", response.body().getMicrowaves());
                mapHash.put("Water Fountain", response.body().getWaterFountains());
                mapHash.put("Nap Pod", response.body().getNapPods());
                loadMarkers(mapHash);

            }

            @Override
            public void onFailure(Call<MapIconResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to retrieve map icons, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });


        liveTrack();
    }

    private void updateLocation(View v) {

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
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onMarkerClick(final Marker marker) {


        if (marker.getTag() == null) {
            return true; //If the live busIcon is selected, dont' do anything.
        }

        Intent popUp;
        clearFocus();
        //marker.setIcon(icon);
        popUp = new Intent(getActivity(), PopUpActivity.class);
        popUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        popUp.putExtra("title", marker.getTitle());
        popUp.putExtra("distance", getDistance(marker.getPosition().latitude, marker.getPosition().longitude));
        popUp.putExtra("id", marker_to_ID.get(marker));
        popUp.putExtra("location", marker.getPosition());
        popUp.putExtra("desc1", markers_to_desc.get(marker).getDesc1());
        popUp.putExtra("desc2", markers_to_desc.get(marker).getDesc2());

        startActivity(popUp);
        return true;
    }

    public void cameraLoadAnimation(LatLng location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));

    }




    public void clearFocus() {
        View current = getActivity().getCurrentFocus();
        if (current != null) {
            current.clearFocus();
        }
        FABmenu.close(true);
        FABmenu.setMenuButtonColorNormalResId(R.color.white);
        FABmenu.getMenuIconView().setImageResource(R.drawable.itemsicons);
    }


    private void loadMarkers(HashMap<String, ArrayList<CategoryLoc>> items) {
        ArrayList<CategoryLoc> microwaves = items.get("Microwave");
        ArrayList<CategoryLoc> waterbottles = items.get("Water Fountain");
        ArrayList<CategoryLoc> sleepPods = items.get("Nap Pod");
        BitmapDescriptor microwaveIcon = BitmapDescriptorFactory.fromResource(R.drawable.microwave_map_icon);
        BitmapDescriptor waterBottleIcon = BitmapDescriptorFactory.fromResource(R.drawable.waterbottle_map_icon);
        BitmapDescriptor sleepPodIcon =BitmapDescriptorFactory.fromResource(R.drawable.sleeppod_map_icon);

        if (microwaves != null && microwaves.size() != 0) {
            for (CategoryLoc loc : microwaves) {
                double lat = loc.getLat();
                double lon = loc.getLon();
                String title = loc.getCategory();
                MarkerOptions singleMarkerOption = new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .icon(microwaveIcon)
                        .visible(false)
                        .title(title);
                Marker singleMarker = mMap.addMarker(singleMarkerOption);
                singleMarker.setTag(false);
                markers_microwave.add(singleMarker);
                markers_to_desc.put(singleMarker, loc);

            }
        }

        if (waterbottles != null && waterbottles.size() != 0) {
            for (CategoryLoc loc : waterbottles) {
                double lat = loc.getLat();
                double lon = loc.getLon();
                String title = loc.getCategory();
                MarkerOptions singleMarkerOption = new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .icon(waterBottleIcon)
                        .visible(false)
                        .title(title);
                Marker singleMarker = mMap.addMarker(singleMarkerOption);
                singleMarker.setTag(false);
                markers_waterbottles.add(singleMarker);
                markers_to_desc.put(singleMarker, loc);
            }
        }

        if (sleepPods != null && sleepPods.size() != 0) {
            for (CategoryLoc loc : sleepPods) {
                double lat = loc.getLat();
                double lon = loc.getLon();
                String title = loc.getCategory();
                MarkerOptions singleMarkerOption = new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .icon(sleepPodIcon)
                        .visible(false)
                        .title(title);
                Marker singleMarker = mMap.addMarker(singleMarkerOption);
                singleMarker.setTag(false);
                markers_sleepPods.add(singleMarker);
                markers_to_desc.put(singleMarker, loc);
            }
        }


    }

    public String getDistance(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(lat, lng, currLocation.latitude, currLocation.longitude, results);
        return DECIMAL_FORMAT.format(results[0] * 0.000621371192) + " mi";
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
        LiveBusActivity.timer = new Timer("liveBus", true);
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

        //starts call to grab list of bus lines - static
        Call<LineResponse> lineCall = bmService.callLineList();
        lineCall.enqueue(new retrofit2.Callback<LineResponse>() {
            @Override
            public void onResponse(Call<LineResponse> call, final Response<LineResponse> response1) {
                if (response1.body() != null && response1.body().getLineRespModels() != null && !response1.body().getLineRespModels().isEmpty()) {
                    //on response, initialize a transformer instance that we will pass in to the next transformer (needed for list of lines / stops)
                    final StopListToLineTransformer sllTransformer = new StopListToLineTransformer();
                    for (LineRespModel lineResponse : response1.body().getLineRespModels()) {
                        ArrayList<StopBeforeTransform> stopsInitial = lineResponse.getLineStops();
                        Line line;
                        if (!stopsInitial.isEmpty()) {
                            //this only exists to call the method stopListToLine which modifies the transformer's instance variables
                            line = sllTransformer.stopListToLine(lineResponse);
                        }
                    }
                    //start new nested call on response to grab a dynamic journey / triplist list
                    Call<TripResponse> tripResponseCall = bmService.callTripList(origin.latitude, origin.longitude, destination.latitude, destination.longitude, convertMillisToUTC(millis));
                    tripResponseCall.enqueue(new retrofit2.Callback<TripResponse>() {
                        @Override
                        public void onResponse(Call<TripResponse> call, Response<TripResponse> response2) {
                            navigation_button.showProgress(false);
                            if (response2.body() != null && response2.body().getTripRespModels() != null && !response2.body().getTripRespModels().isEmpty()) {
                                //add all journeys to this final routeList
                                ArrayList<Journey> routeList = new ArrayList<>();
                                //for all the trip lists in the response, we create a journey
                                for (TripRespModel tripResponse : response2.body().getTripRespModels()) {
                                    ArrayList<TripBeforeTransform> tripsInitial = tripResponse.getTripList();
                                    Journey journey;
                                    if (!tripsInitial.isEmpty()) {
                                        TripListToJourneyTransformer tljtransformer = new TripListToJourneyTransformer();
                                        try {
                                            //create the journey by passing in the trip list and first transformer instance to new transformer
                                            journey = tljtransformer.tripListToJourney(tripResponse, sllTransformer);
                                            //add it to routeList
                                            routeList.add(journey);
                                        } catch (ParseException e) {
                                            Toast.makeText(getActivity(), "Unexpected error encountered.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                //start new activity with populated routeList
                                Intent routeSelect = new Intent(getContext(), RouteSelectActivity.class);
                                routeSelect.putExtra("routes", routeList);
                                startActivity(routeSelect);
                            } else {
                                Toast.makeText(getActivity(), "Problem retrieving routes.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<TripResponse> call, Throwable t) {

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Error retrieving lines.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LineResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error retrieving lines.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String convertMillisToUTC(Long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(millis));
    }



    public LatLng getCurrLocation() {
        return currLocation;
    }


    @Override
    public void onMapLoaded() {

    }



}