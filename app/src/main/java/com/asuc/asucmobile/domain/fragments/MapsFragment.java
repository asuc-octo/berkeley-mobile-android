package com.asuc.asucmobile.domain.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.LiveBusController;
import com.asuc.asucmobile.domain.main.CreditsDialog;
import com.asuc.asucmobile.domain.main.LiveBusActivity;
import com.asuc.asucmobile.domain.main.PopUpActivity;
import com.asuc.asucmobile.domain.main.RouteSelectActivity;
import com.asuc.asucmobile.domain.models.Buses;
import com.asuc.asucmobile.domain.models.CategoryLoc;
import com.asuc.asucmobile.domain.models.Journey;
import com.asuc.asucmobile.domain.models.Line;
import com.asuc.asucmobile.domain.models.LineRespModel;
import com.asuc.asucmobile.domain.models.TripRespModel;
import com.asuc.asucmobile.domain.models.responses.LineResponse;
import com.asuc.asucmobile.domain.models.responses.TripResponse;
import com.asuc.asucmobile.domain.repository.MultiRepository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.domain.services.BMService;
import com.asuc.asucmobile.infrastructure.models.StopBeforeTransform;
import com.asuc.asucmobile.infrastructure.models.TripBeforeTransform;
import com.asuc.asucmobile.infrastructure.transformers.StopListToLineTransformer;
import com.asuc.asucmobile.infrastructure.transformers.TripListToJourneyTransformer;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.RoundedBackgroundSpan;
import com.asuc.asucmobile.values.MapIconCategories;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


public class MapsFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLoadedCallback {

    private static final String TAG = "MapsFragment";

    @Inject
    BMService bmService;
    @Inject
    MultiRepository<String, CategoryLoc> repository;

    private GoogleMap mMap;
    Gson gson = new Gson();
    final LatLng BERKELEY = new LatLng(37.8716, -122.2727);
    final LatLng EGG = new LatLng(37.8266, -122.3236);
    final String GO_BEARS = "go bears!";
    private GoogleApiClient googleApiClient;
    double longitude;
    double latitude;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    LatLng currLocation;
    View mapView;
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    com.google.android.material.floatingactionbutton.FloatingActionButton navigation_button;
    FloatingActionMenu FABmenu;

    private boolean bottlesShown = false;
    private boolean sleepShown = false;
    private boolean microwavesShown = false;
    private boolean mentalHealthsShown = false;
    private boolean printersShown = false;
    private boolean bikesShown = false;

    private Map<String, Boolean> iconsShown;

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
    ArrayList<Marker> markers_microwaves = new ArrayList<>();
    ArrayList<Marker> markers_printers = new ArrayList<>();
    ArrayList<Marker> markers_mentals = new ArrayList<>();
    ArrayList<Marker> markers_bikes = new ArrayList<>();
    ArrayList<Marker> all_markers = new ArrayList<>();

    private Map<String, ArrayList<Marker>> markersMap;

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
    private FloatingActionButton microwave, sleepPod, waterBottle, printers, mentalHealths, bikes;
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

        initHashMaps(); // lel

        GlobalApplication.getRepositoryComponent().inject(this);

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
//        mapFragment.getMapAsync(this);

        refreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);


        final DestinationFragment searchBar = (DestinationFragment) getActivity().getFragmentManager().findFragmentById(R.id.destination_bar);
        final OriginFragment originBar = (OriginFragment) getActivity().getFragmentManager().findFragmentById(R.id.origin_bar);
        navigation_button = (com.google.android.material.floatingactionbutton.FloatingActionButton) layout.findViewById(R.id.determinate);
        FABmenu = (FloatingActionMenu) layout.findViewById(R.id.FABmenu);

        sleepPod = (FloatingActionButton) layout.findViewById(R.id.sleeppod);
        waterBottle = (FloatingActionButton) layout.findViewById(R.id.waterbottle);
        microwave = (FloatingActionButton) layout.findViewById(R.id.microwave);
        bikes = (FloatingActionButton) layout.findViewById(R.id.bike);
        mentalHealths = (FloatingActionButton) layout.findViewById(R.id.mental_health);
        printers = (FloatingActionButton) layout.findViewById(R.id.printer);

        // get Remote Config values for spotlight
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        showSpotlight = mFirebaseRemoteConfig.getBoolean(SHOW_SPOTLIGHT);

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
                    // Note: Firebase events must be under 40 characters in length
                    mFirebaseAnalytics.logEvent("view_map_icons_after_first_session", bundle);
                }

                if (FABmenu.isOpened()) {
                    FABmenu.close(true);
                    FABmenu.setMenuButtonColorNormalResId(R.color.white);
                    FABmenu.getMenuIconView().setImageResource(R.drawable.itemsicons);
                } else {
                    FABmenu.open(true);
                    FABmenu.setMenuButtonColorNormalResId(R.color.dark_blue);
                    FABmenu.getMenuIconView().setImageResource(R.drawable.items_icon_pressed);
                }
            }
        });

        setFABListener(sleepPod, "nappod", MapIconCategories.NAP_POD);
        setFABListener(waterBottle, "waterbottles", MapIconCategories.WATER_FOUNTAIN);
        setFABListener(microwave, "microwaves", MapIconCategories.MICROWAVE);
        setFABListener(printers, "printers", MapIconCategories.PRINTER);
        setFABListener(bikes, "bikes", MapIconCategories.FORD_GO_BIKE);
        setFABListener(mentalHealths, "mentalhealths", MapIconCategories.MENTAL_HEALTH);

        navigation_button.setOnClickListener(new View.OnClickListener() {
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

                    if (viewedFirstSession()) {
                        mFirebaseAnalytics.logEvent("map_icon_clicked_after_first_session", bundle);
                    }

                    for (Marker marker : markers_sleepPods) {
                        marker.setVisible(!sleepShown);
                    }
                }

            }
        });

        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                layout.getWindowVisibleDisplayFrame(r);

                int heightDiff = layout.getBottom() - r.bottom;

                if (heightDiff <= 0) heightDiff = 0;
                else heightDiff += 70;

                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    int suggestionsBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

                    navigation_button.setTranslationY(-(heightDiff + suggestionsBarHeight));
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
     * Set map icon FAB on click listeners
     * @param fab
     * @param bund
     * @param category
     */
    private void setFABListener(FloatingActionButton fab, final String bund, final String category) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MapsFragment.this.getContext());
                Bundle bundle = new Bundle();
                bundle.putString("Category", bund);
                mFirebaseAnalytics.logEvent("map_icon_clicked", bundle);
                updateLocation(v);

                if (viewedFirstSession()) {
                    mFirebaseAnalytics.logEvent("map_icon_clicked_after_first_session", bundle);
                }

                //updateLocation(v);
                for (Marker marker : markers_waterbottles) {
                    marker.setVisible(false);
                }

                for (String key : markersMap.keySet()) {
                    boolean shown = false;
                    if (key.equals(category)) { // invert if this, otherwise invis
                        shown = !iconsShown.get(category);
                    }
                    iconsShown.put(key, shown);

                    for (Marker marker : markersMap.get(key)) {
                        marker.setVisible(shown);
                    }
                }
            }
        });
    }

    /**
     * Store all CategoryLoc metadata in hashmaps keyed on their category name
     */
    private void initHashMaps() {
        markersMap = new HashMap<>();
        markersMap.put(MapIconCategories.FORD_GO_BIKE, new ArrayList<Marker>());
        markersMap.put(MapIconCategories.MENTAL_HEALTH, new ArrayList<Marker>());
        markersMap.put(MapIconCategories.MICROWAVE, new ArrayList<Marker>());
        markersMap.put(MapIconCategories.NAP_POD, new ArrayList<Marker>());
        markersMap.put(MapIconCategories.PRINTER, new ArrayList<Marker>());
        markersMap.put(MapIconCategories.WATER_FOUNTAIN, new ArrayList<Marker>());

        iconsShown = new HashMap<>();
        iconsShown.put(MapIconCategories.FORD_GO_BIKE, false);
        iconsShown.put(MapIconCategories.MENTAL_HEALTH, false);
        iconsShown.put(MapIconCategories.MICROWAVE, false);
        iconsShown.put(MapIconCategories.NAP_POD, false);
        iconsShown.put(MapIconCategories.PRINTER, false);
        iconsShown.put(MapIconCategories.WATER_FOUNTAIN, false);
    }

    /**
     * Check the SharedPreferences manager for the VIEWED_FIRST_SESSION key
     *
     * @return whether or not the user viewed the first session or not
     */
    private boolean viewedFirstSession() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getBoolean(MapsFragment.VIEWED_FIRST_SESSION, false);
    }

    /**
     * Register that the user has completed the first session in the VIEWED_FIRST_SESSION key
     */
    public void registerSession() {
        // Save viewed spotlight value
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        sharedPreferencesEditor.putBoolean(VIEWED_FIRST_SESSION, true);
        sharedPreferencesEditor.apply();
    }

    /**
     * Show the spotlight on a target View
     *
     * @param target the View that the Spotlight will highlight
     */
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

        refreshMapIcons();

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
                    navigation_button = layout.findViewById(R.id.determinate);

                }
                navigation_button.show();
                hideKeyboard(mapView);
                clearFocus();
            }
        });

        //Changes the FAB (my location button) to bottom right
        moveNavigationIcon(mapView);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(this);

        liveTrack();
        shh();

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

        Log.d("HELLOOO", "MARKER CLICKED");

        if (marker.getTag() == null) {
            return true; //If the live busIcon is selected, dont' do anything.
        }

        // shhh
        if (marker.getTitle().equals(GO_BEARS)) {
            Intent i = new Intent(getActivity(), CreditsDialog.class);
            startActivity(i);
            return true;
        }

//        Intent popUp;
        clearFocus();

        Bundle bundle = new Bundle();
        bundle.putString("title", marker.getTitle());
        bundle.putString("distance", getDistance(marker.getPosition().latitude, marker.getPosition().longitude));
        bundle.putString("id", marker_to_ID.get(marker));
        bundle.putParcelable("location", marker.getPosition());
        bundle.putString("desc1", markers_to_desc.get(marker).getDesc1());
        bundle.putString("desc2", markers_to_desc.get(marker).getDesc2());

        PopUpActivity newDialog = new PopUpActivity();

        newDialog.setArguments(bundle);
        newDialog.show(getActivity().getFragmentManager(), "dialog");

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

    private void putMarkers(ArrayList<CategoryLoc> categoryLocs, BitmapDescriptor bitmapDescriptor, ArrayList<Marker> markers) {
        if (categoryLocs != null && categoryLocs.size() != 0) {
            for (CategoryLoc loc : categoryLocs) {
                double lat = loc.getLat();
                double lon = loc.getLon();
                String title = loc.getCategory();
                MarkerOptions singleMarkerOption = new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .icon(bitmapDescriptor)
                        .visible(false)
                        .title(title);
                Marker singleMarker = mMap.addMarker(singleMarkerOption);
                singleMarker.setTag(false);
                markers.add(singleMarker);
                markers_to_desc.put(singleMarker, loc);
            }
        }
    }

    private void loadMarkers(HashMap<String, ArrayList<CategoryLoc>> items) {
        ArrayList<CategoryLoc> microwaves = items.get(MapIconCategories.MICROWAVE);
        ArrayList<CategoryLoc> waterbottles = items.get(MapIconCategories.WATER_FOUNTAIN);
        ArrayList<CategoryLoc> sleepPods = items.get(MapIconCategories.NAP_POD);
        ArrayList<CategoryLoc> mentalHealths = items.get(MapIconCategories.MENTAL_HEALTH);
        ArrayList<CategoryLoc> bikes = items.get(MapIconCategories.FORD_GO_BIKE);
        ArrayList<CategoryLoc> printers = items.get(MapIconCategories.PRINTER);
        BitmapDescriptor microwaveIcon = BitmapDescriptorFactory.fromResource(R.drawable.microwave_map_icon);
        BitmapDescriptor waterBottleIcon = BitmapDescriptorFactory.fromResource(R.drawable.waterbottle_map_icon);
        BitmapDescriptor sleepPodIcon = BitmapDescriptorFactory.fromResource(R.drawable.sleeppod_map_icon);
        BitmapDescriptor mentalHealthIcon = BitmapDescriptorFactory.fromResource(R.drawable.mental_health_icon);
        BitmapDescriptor bikeIcon = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
        BitmapDescriptor printerIcon = BitmapDescriptorFactory.fromResource(R.drawable.printer_icon);

        putMarkers(microwaves, microwaveIcon, markersMap.get(MapIconCategories.MICROWAVE));
        putMarkers(waterbottles, waterBottleIcon, markersMap.get(MapIconCategories.WATER_FOUNTAIN));
        putMarkers(sleepPods, sleepPodIcon, markersMap.get(MapIconCategories.NAP_POD));
        putMarkers(mentalHealths, mentalHealthIcon, markersMap.get(MapIconCategories.MENTAL_HEALTH));
        putMarkers(bikes, bikeIcon, markersMap.get(MapIconCategories.FORD_GO_BIKE));
        putMarkers(printers, printerIcon, markersMap.get(MapIconCategories.PRINTER));

        all_markers = new ArrayList<>();
        all_markers.addAll(markers_microwaves);
        all_markers.addAll(markers_waterbottles);
        all_markers.addAll(markers_sleepPods);
        all_markers.addAll(markers_mentals);
        all_markers.addAll(markers_bikes);
        all_markers.addAll(markers_printers);

    }

    private void shh() {
        BitmapDescriptor bearIcon = BitmapDescriptorFactory.fromResource(R.drawable.bearmarker);
        MarkerOptions singleMarkerOption = new MarkerOptions()
                .position(EGG)
                .icon(bearIcon)
                .visible(true)
                .title(GO_BEARS);
        Marker singleMarker = mMap.addMarker(singleMarkerOption);
        singleMarker.setTag(false);
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
        navigation_button.setEnabled(false);
        navigation_button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
//        navigation_button.showProgress(true);

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
//                            navigation_button.showProgress(false);
                            navigation_button.setEnabled(true);
                            navigation_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2685F5")));
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
                            navigation_button.setEnabled(true);
                            navigation_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2685F5")));
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Error retrieving lines.", Toast.LENGTH_SHORT).show();
                    navigation_button.setEnabled(true);
                    navigation_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2685F5")));
                }
            }

            @Override
            public void onFailure(Call<LineResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error retrieving lines.", Toast.LENGTH_SHORT).show();
                navigation_button.setEnabled(true);
                navigation_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2685F5")));
            }
        });

    }

    private void refreshMapIcons() {
        // initialize the hashmap
        mapHash.put(MapIconCategories.MENTAL_HEALTH, new ArrayList<>());
        mapHash.put(MapIconCategories.MICROWAVE, new ArrayList<>());
        mapHash.put(MapIconCategories.NAP_POD, new ArrayList<>());
        mapHash.put(MapIconCategories.PRINTER, new ArrayList<>());
        mapHash.put(MapIconCategories.WATER_FOUNTAIN, new ArrayList<>());
        mapHash.put(MapIconCategories.FORD_GO_BIKE, new ArrayList<>());

        final AtomicInteger count = new AtomicInteger();

        repository.scanAll(mapHash, new RepositoryCallback<CategoryLoc>() {
            @Override
            public void onSuccess() {
                if (count.addAndGet(1) == mapHash.keySet().size()) {
                    loadMarkers(mapHash); // TODO: want to load markers after all repositories scanned, but issue since async. This loads markers multiple times...
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Unable to retrieve map icons, please try again",
                        Toast.LENGTH_SHORT).show();
                if (count.addAndGet(1) == mapHash.keySet().size()) {
                    loadMarkers(mapHash); // TODO: want to load markers after all repositories scanned, but issue since async. This loads markers multiple times...
                }
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
