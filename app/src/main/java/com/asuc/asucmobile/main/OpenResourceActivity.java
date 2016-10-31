package com.asuc.asucmobile.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.ResourceController;
import com.asuc.asucmobile.models.Resource;
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

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenResourceActivity extends BaseActivity {

    private static final HashSet<String> NULL_STRINGS = new HashSet<String>() {{
        add("");
        add("null");
        add("N/A");
    }};
    private static final String PHONE_REGEX =
            "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";
    private static final int REQUEST_CODE_PLAY_SERVICES = 1;

    private MapFragment mapFragment;
    private GoogleMap map;
    private Resource resource;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_resource);
        exitIfNoData();
        setupToolbar(resource.getResource(), true);

        // Populate UI.
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView phone = (TextView) findViewById(R.id.phone);
        TextView email = (TextView) findViewById(R.id.email);
        TextView location = (TextView) findViewById(R.id.location);
        TextView notes = (TextView) findViewById(R.id.notes);
        LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout notesLayout = (LinearLayout) findViewById(R.id.notes_layout);
        View dividerHoursPhone = findViewById(R.id.divider_hours_phone);
        View dividerPhoneEmail = findViewById(R.id.divider_phone_email);
        View dividerEmailLocation = findViewById(R.id.divider_email_location);
        View dividerLocationNotes = findViewById(R.id.divider_location_notes);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        //This escape fixes a bug I had where the Tang center would display \n instead of newlines.
        setText(new View[] { hoursLayout, dividerHoursPhone }, hours,
                resource.getHours().replace("\\n", System.getProperty("line.separator")));
        setText(new View[] { phoneLayout, dividerPhoneEmail }, phone, setUpPhone());
        setText(new View[] { emailLayout, dividerEmailLocation }, email, resource.getEmail());

        //Escape for Confidential Care Advocates which has a null location.
        setText(new View[] { locationLayout, dividerLocationNotes },location,
                resource.getLocation());
        setText(new View[] { notesLayout, dividerLocationNotes }, notes, setUpNotes());

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + resource.getPhone1()));
                startActivity(i);
            }
        });
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[] { resource.getEmail() });
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        // Display instructions if this is the first time opening this activity.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = sharedPref.getBoolean("resource_initial", true);
        if (firstTime) {
            Toast.makeText(this, "Tap on the location for directions,\nor the phone to dial!",
                    Toast.LENGTH_LONG).show();
            sharedPref.edit().putBoolean("resource_initial", false).apply();
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
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        map = googleMap;
                        BitmapDescriptor bitmap =
                                BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                        map.addMarker(new MarkerOptions()
                                .position(resource.getCoordinates())
                                .icon(bitmap)
                                .title(resource.getResource())
                        );
                        map.getUiSettings().setZoomControlsEnabled(false);
                        map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(resource.getCoordinates(),17));
                        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                openMap();
                            }
                        });
                    }
                }
            });
        }
    }

    private void openMap() {
        double lat = resource.getCoordinates().latitude;
        double lng = resource.getCoordinates().longitude;
        String uri = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?dirflg=w&saddr=Current+Location&daddr=%f,%f", lat, lng
        );
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    /**
     * This code sets up daily hours, only.
     */
    private String setUpPhone() {
        if (validatePhoneNumber(resource.getPhone2())) {
            return "Phone 1: " + resource.getPhone1() + "\n" + "Phone 2: " + resource.getPhone2();
        } else {
            return resource.getPhone1();
        }
    }

    private String setUpNotes() {
        String displayedNotes =
                !resource.getNotes().equals("null") ? "\n" + resource.getNotes() : "";
        return "This is an " + resource.getOnOrOffCampus() + " resource. " + displayedNotes;
    }

    private void exitIfNoData() {
        resource = ((ResourceController) ResourceController.getInstance()).getCurrentResource();
        if (resource == null) {
            finish();
        }
    }

    private boolean validatePhoneNumber(String num) {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }

    /**
     * Sets the text for a view if valid text is present. Also strips unneeded whitespace from text.
     * @param viewsToBeDeleted Views to delete if text is invalid.
     * @param view TextView to set the text on.
     * @param text Content string.
     */
    private void setText(View[] viewsToBeDeleted, TextView view, String text) {
        if (NULL_STRINGS.contains(text)) {
            for (View v : viewsToBeDeleted) {
                v.setVisibility(View.GONE);
            }
        } else {
            view.setText(text.trim());
        }
    }

}