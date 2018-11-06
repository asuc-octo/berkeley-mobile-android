package com.asuc.asucmobile.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenResourceActivity extends BaseActivity {

    private static final LatLng CAMPUS_LOCATION = new LatLng(37.871899, -122.25854);
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
    private static Resource resource;

    public static OpenResourceActivity selfReference;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_resource);
        exitIfNoData();
        setupToolbar(resource.getResource(), true);
        selfReference = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("opened_resource", resource.getResource());
        mFirebaseAnalytics.logEvent("opened_resource", bundle);

        // Populate UI.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);


        // set description
        setUpDescription();
        setUpNotes();
        setUpLocation();
        setUpEmail();
        setUpHours();
        setUpPhone();

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

    private void setUpNotes(){
        TextView notes = (TextView) findViewById(R.id.notes);
        LinearLayout notesLayout = (LinearLayout) findViewById(R.id.notes_layout);
        setText(notesLayout, notes, getNotesString());
    }


    private void setUpLocation(){
        TextView location = (TextView) findViewById(R.id.location);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        if (setText(locationLayout, location, resource.getLocation())) {
            locationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMap();
                }
            });
        }
    }

    private void setUpEmail(){
        TextView email = (TextView) findViewById(R.id.email);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        if (setText(emailLayout, email, resource.getEmail())) {
            emailLayout.setOnClickListener(
                    new View.OnClickListener() {
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
        }
    }

    private void setUpPhone(){
        TextView phone = (TextView) findViewById(R.id.phone);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);

        if (setText(phoneLayout, phone, getPhoneNumbersString())) {
            phoneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + resource.getPhone1()));
                    startActivity(i);
                }
            });
        }
    }

    private void setUpHours(){
        TextView hours = (TextView) findViewById(R.id.hours);
        LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);
        setText(hoursLayout, hours, resource.getHours());

    }

    private void setUpDescription() {
        TextView description = (TextView) findViewById(R.id.description);
        LinearLayout descriptionLayout = (LinearLayout) findViewById(R.id.description_layout);
        if (setText(descriptionLayout, description, resource.getDescription())) {
            descriptionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("BM description", resource.getDescription());
                    clipboard.setPrimaryClip(clip);
                }
            });
        }
        if (NULL_STRINGS.contains(resource.getNotes())) {
            findViewById(R.id.divider_description).setVisibility(View.GONE);
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
                        map.getUiSettings().setZoomControlsEnabled(false);
                        if (resource.getCoordinates() != null) {
                            BitmapDescriptor bitmap =
                                    BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                            map.addMarker(new MarkerOptions()
                                    .position(resource.getCoordinates())
                                    .icon(bitmap)
                                    .title(resource.getResource())
                            );
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(resource.getCoordinates(), 17));
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
    private String getPhoneNumbersString() {
        if (validatePhoneNumber(resource.getPhone2())) {
            return "Phone 1: " + resource.getPhone1() + "\n" + "Phone 2: " + resource.getPhone2();
        } else {
            return resource.getPhone1();
        }
    }


    private String getNotesString() {
        String s = resource.getNotes();
        if (s != null && !s.equals("")) {
            String displayedNotes =
                    !resource.getNotes().equals("null") ? "\n" + resource.getNotes() : "";
            return "This is an " + resource.getOnOrOffCampus() + " resource. " + displayedNotes;
        }
        return "";
    }

    public static void setResource(Resource r) {
        resource = r;
    }

    private void exitIfNoData() {
        if (resource == null) {
            finish();
        }
    }

    private boolean validatePhoneNumber(String num) {
        if (num == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }

    /**
     * Sets the text for a view if valid text is present. Also strips unneeded whitespace from text.
     * Returns whether text was valid
     * @param viewToBeDeleted Views to delete if text is invalid.
     * @param view TextView to set the text on.
     * @param text Content string.
     */
    private boolean setText(View viewToBeDeleted, TextView view, String text) {
        if (text == null || NULL_STRINGS.contains(text)) {
            viewToBeDeleted.setVisibility(View.GONE);
            return false;
        } else {
            view.setText(text.trim());
            return true;
        }
    }

}