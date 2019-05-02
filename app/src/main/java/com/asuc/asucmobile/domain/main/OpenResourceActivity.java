package com.asuc.asucmobile.domain.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.asuc.asucmobile.domain.models.Resource;
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
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private MapFragment mapFragment;
    private GoogleMap map;
    private static Resource resource;

    public static OpenResourceActivity selfReference;
    private FirebaseAnalytics mFirebaseAnalytics;

    private ViewGroup.LayoutParams hoursParams;



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

    /**
     * This code sets up daily hours, only.
     */
    private void setUpHours() {
        final TextView hours = (TextView) findViewById(R.id.hours);
        final TextView hours_expand = (TextView) findViewById(R.id.hours_expand);
        final LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);

        // weekly resource hours should already be set up when you open the page
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
        ArrayList<Date> openings = resource.getWeeklyOpen();
        Spanned weeklyHoursString = new SpannableString("Today\n");
        for (int i=0; i < openings.size(); i++) {
            Spannable hoursString;
            hoursString = new SpannableString("\n" + resource.getDayOfWeek(i));
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
        ArrayList<Date> openings = resource.getWeeklyOpen();
        ArrayList<Date> closings = resource.getWeeklyClose();
        ArrayList<Boolean> byAppointments = resource.getWeeklyAppointments();
        Spannable hoursString;
        if (resource.isByAppointment()) {
            hoursString = new SpannableString("BY APPOINTMENT\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light )), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (openings != null && closings != null) {
            String isOpen;
            int color;
            if (resource.isOpen()) {
                isOpen = "OPEN";
                color = ContextCompat.getColor(getApplicationContext(),R.color.green);
            } else {
                isOpen = "CLOSED";
                color = ContextCompat.getColor(getApplicationContext(),R.color.red);
            }

            hoursString = new SpannableString(isOpen + "\n");
            hoursString.setSpan(new ForegroundColorSpan(color), 0, hoursString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
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