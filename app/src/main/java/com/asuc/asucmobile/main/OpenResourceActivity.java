package com.asuc.asucmobile.main;

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
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenResourceActivity extends BaseActivity {

    private static final int REQUEST_CODE_PLAY_SERVICES = 1;
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private MapFragment mapFragment;
    private GoogleMap map;
    private Resource resource;
    private ViewGroup.LayoutParams hoursParams;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_resource);
        exitIfNoData();
        setupToolbar(resource.getResource(), true);

        // Populate UI.
        final TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);
        TextView notes = (TextView) findViewById(R.id.notes);
        final LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        LinearLayout notesLayout = (LinearLayout) findViewById(R.id.notes_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        //This escape fixes a bug I had where the Tang center would display \n instead of newlines.
        hours.setText(resource.getHours().replace("\\n", System.getProperty("line.separator")));

        //Escape for Confidential Care Advocates which has a null location.
        address.setText(resource.getLocation() != "null" ? resource.getLocation() : "");
        phone.setText(setUpPhone());
        notes.setText(setUpNotes());

        //Wrap hours around the text.
        hoursLayout.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
        notesLayout.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + resource.getPhone1()));
                startActivity(i);
            }
        });

        // Display instructions if this is the first time opening this activity.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = sharedPref.getBoolean("resource_initial", true);
        if (firstTime) {
            Toast.makeText(this, "Tap on the location for directions,\nor the phone to dial!", Toast.LENGTH_LONG).show();
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
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                        map.addMarker(new MarkerOptions()
                                .position(resource.getCoordinates())
                                .icon(bitmap)
                                .title(resource.getResource())
                        );
                        map.getUiSettings().setZoomControlsEnabled(false);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(resource.getCoordinates(), 17));
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
        String displayedNotes = resource.getNotes() != "null" ? "\n" + resource.getNotes() : "";
        return "This is an " + resource.getOnOrOffCampus() + " resource. " +
                displayedNotes;
    }

    private void exitIfNoData() {
        resource = ((ResourceController) ResourceController.getInstance(this)).getCurrentResource();
        if (resource == null) {
            finish();
        }
    }

    private boolean validatePhoneNumber(String num) {
        Pattern pattern = Pattern.compile("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$");
        Matcher matcher = pattern.matcher(num);

        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}