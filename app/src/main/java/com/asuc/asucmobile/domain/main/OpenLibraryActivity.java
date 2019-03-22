package com.asuc.asucmobile.domain.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.models.Library;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OpenLibraryActivity extends BaseActivity {

    public static final String TAG = "OpenLibraryActivity";

    private static final LatLng CAMPUS_LOCATION = new LatLng(37.871899, -122.25854);
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private static final int REQUEST_CODE_PLAY_SERVICES = 1;

    private MapFragment mapFragment;
    private GoogleMap map;
    private static Library library;
    private ViewGroup.LayoutParams hoursParams;
    public static OpenLibraryActivity self_reference;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_library);
        exitIfNoData();
        setupToolbar(library.getName(), true);
        self_reference = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("opened_library", library.getName());
        mFirebaseAnalytics.logEvent("opened_library", bundle);

        // Populate UI.
        setUpPhone();
        setUpHours();
        setUpMap();
        setUpBooking();

        // Display instructions if this is the first time opening this activity.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = sharedPref.getBoolean("library_initial", true);
        if (firstTime) {
            Toast.makeText(this, "Tap on the location for directions,\nor the phone to dial!", Toast.LENGTH_LONG).show();
            sharedPref.edit().putBoolean("library_initial", false).apply();
        }
        setUpMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
        setUpMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PLAY_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                        this,
                        "Google Play Services must be installed to display map.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setUpPhone() {
        TextView phone = (TextView) findViewById(R.id.phone);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);

        if (library.getPhone() == null || library.getPhone().equals("")) {
            phone.setText("No Phone Number");
            return;
        }

        phone.setText(library.getPhone());
        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + library.getPhone()));
                startActivity(i);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setUpMap() {
        TextView address = (TextView) findViewById(R.id.location);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        address.setText(library.getLocation());
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

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
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        map = googleMap;
                        map.getUiSettings().setZoomControlsEnabled(false);
                        if (library.getCoordinates() != null) {
                            BitmapDescriptor bitmap =
                                    BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                            map.addMarker(new MarkerOptions()
                                    .position(library.getCoordinates())
                                    .icon(bitmap)
                                    .title(library.getName())
                            );
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(library.getCoordinates(), 17));
                            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    openMap();
                                }
                            });
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CAMPUS_LOCATION, 14));
                        }
                    }
                }
            });
        }
    }

    private void openMap() {
        double lat = library.getCoordinates().latitude;
        double lng = library.getCoordinates().longitude;
        String uri = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?dirflg=w&saddr=Current+Location&daddr=%f,%f", lat, lng
        );
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    private void setUpBooking() {
        TextView booking = (TextView) findViewById(R.id.booking);
        LinearLayout bookingLayout = (LinearLayout) findViewById(R.id.booking_layout);

        //Should be updated to pull from the backend once that is set up
        ArrayList<String> bookableLibraries = new ArrayList<>();
        bookableLibraries.add("East Asian Library");
        bookableLibraries.add("Engineering Library");
        bookableLibraries.add("Environmental Design Library");
        bookableLibraries.add("Earth Sciences & Map Library");
        bookableLibraries.add("Main (Gardner) Stacks");
        bookableLibraries.add("Moffitt Library");
        bookableLibraries.add("Institute of Governmental Studies Library");
        final String name = library.getName();
        if (!bookableLibraries.contains(name)) {
            booking.setText("Cannot reserve study rooms at this library");
            return;
        }
        booking.setText("Reserve a study room");

        bookingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //default for libraries with multiple links
                String uri = "https://berkeley.libcal.com/";

                if (name.equals("Engineering Library")) {
                    uri = "https://berkeley.libcal.com/booking/engi";
                } else if (name.equals("East Asian Library")) {
                    uri = "https://berkeley.libcal.com/booking/eal";
                } else if (name.equals("Earth Sciences & Map Library")) {
                    uri = "https://berkeley.libcal.com/booking/EART";
                } else if (name.equals("Main (Gardner) Stacks")) {
                    uri = "https://berkeley.libcal.com/booking/gardner";
                } else if (name.equals("Institute of Governmental Studies Library")) {
                    uri = "https://berkeley.libcal.com/booking/igs";
                }
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(i);
            }
        });
    }

    /**
     * This code sets up daily hours, only.
     */
    private void setUpHours() {
        final TextView hours = (TextView) findViewById(R.id.hours);
        final TextView hours_expand = (TextView) findViewById(R.id.hours_expand);
        final LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);

        // weekly library hours should already be set up when you open the page
        hours.setText(setUpWeeklyHoursLeft());
        hoursParams = hoursLayout.getLayoutParams();
        hoursLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
        hours_expand.setText(setUpWeeklyHoursRight());
    }

    /**
     * This function is called with setUpWeeklyHoursRight() to set up the weekly hours
     * page, which essentially appears as left and right justified text. The left side
     * holds days of the week and some description.
     */
    private Spanned setUpWeeklyHoursLeft() {
        ArrayList<Date> openings = library.getWeeklyOpen();
        Spanned weeklyHoursString = new SpannableString("Today\n");
        for (int i=0; i < openings.size(); i++) {
            Spannable hoursString;
            hoursString = new SpannableString("\n" + library.getDayOfWeek(i));
            if (i == 0) {
                hoursString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, hoursString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }
        return weeklyHoursString;
    }

    /**
     * This function is called with setUpWeeklyHoursLeft() to set up the weekly hours
     * page, which essentially appears as left and right justified text. The right side
     * holds hours for each day of the week and some description.
     */
    private Spanned setUpWeeklyHoursRight() {
        ArrayList<Date> openings = library.getWeeklyOpen();
        ArrayList<Date> closings = library.getWeeklyClose();
        ArrayList<Boolean> byAppointments = library.getWeeklyAppointments();
        Spannable hoursString;
        if (library.isByAppointment()) {
//            hoursString = new SpannableString("BY APPOINTMENT  ▲\n");
            hoursString = new SpannableString("BY APPOINTMENT\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light )), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (openings != null && closings != null) {
            String isOpen;
            int color;
            if (library.isOpen()) {
//                isOpen = "OPEN  ▲";
                isOpen = "OPEN";
                color = ContextCompat.getColor(getApplicationContext(),R.color.green);
            } else {
//                isOpen = "CLOSED  ▲";
                isOpen = "CLOSED";
                color = ContextCompat.getColor(getApplicationContext(),R.color.red);
            }

            hoursString = new SpannableString(isOpen + "\n");
            hoursString.setSpan(new ForegroundColorSpan(color), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
//            hoursString = new SpannableString("CLOSED ALL DAY  ▲\n");
            hoursString = new SpannableString("CLOSED ALL DAY\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        Spanned weeklyHoursString = hoursString;
        for (int i=0; i < openings.size(); i++) {
            if (byAppointments.get(i)) {
                hoursString = new SpannableString("\n  BY APPOINTMENT");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (openings.get(i) != null && closings.get(i) != null) {
                String opening = HOURS_FORMAT.format(openings.get(i));
                String closing = HOURS_FORMAT.format(closings.get(i));
                hoursString = new SpannableString("\n" + opening + " - " + closing);
            } else {
                hoursString = new SpannableString("\n  CLOSED ALL DAY");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if (i == 0) {
                hoursString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, hoursString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }
        return weeklyHoursString;
    }

    /**
     * Set the library we are currently considering. Call before opening this activitiy
     * @param l
     */
    public static void setLibrary(Library l) {
        library = l;
        Log.d(TAG, library.toString());
    }

    private void exitIfNoData() {
        if (library == null) {
            finish();
        }
    }

}