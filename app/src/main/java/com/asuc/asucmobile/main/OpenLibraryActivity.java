package com.asuc.asucmobile.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.ImageDownloadThread;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OpenLibraryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PLAY_SERVICES = 1;
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    private MapFragment mapFragment;
    private GoogleMap map;
    private Library library;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        library = ((LibraryController) LibraryController.getInstance(this)).getCurrentLibrary();
        if (library == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_open_library);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(library.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView hours = (TextView) findViewById(R.id.hours);
        final TextView hours_expand = (TextView) findViewById(R.id.hours_expand);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);

        final LinearLayout hoursLayout = (LinearLayout) findViewById(R.id.hours_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        hours.setText(setUpHours());
        address.setText(library.getLocation());
        phone.setText(library.getPhone());

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        hoursLayout.setOnClickListener(new com.asuc.asucmobile.utilities.hoursOnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(status);

                if (status == false) {
                    hours.setText(setUpWeeklyHoursLeft());
                    hours.setGravity(Gravity.CENTER_VERTICAL);
                    hoursLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
                    hours_expand.setText(setUpWeeklyHoursRight());
                    status = true;
                } else {
                    hours.setText(setUpHours());
                    hours.setGravity(Gravity.CENTER_VERTICAL);
                    hours_expand.setText("");
                    hoursLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
                    status = false;
                }
            }
        });

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + library.getPhone()));
                startActivity(i);
            }
        });

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

        library = ((LibraryController) LibraryController.getInstance(this)).getCurrentLibrary();
        if (library == null) {
            finish();
        }

        setUpMap();
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
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
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                map.addMarker(new MarkerOptions()
                        .position(library.getCoordinates())
                        .icon(bitmap)
                        .title(library.getName())
                );
                map.getUiSettings().setZoomControlsEnabled(false);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(library.getCoordinates(), 17));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        openMap();
                    }
                });
            }
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

    private Spannable setUpHours() {
        /*
           This code sets up daily hours, only.
         */
        Spannable hoursString;
        if (library.isByAppointment()) {
            hoursString = new SpannableString("Today ▼  BY APPOINTMENT");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light)), 9, 23, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (library.getOpening() != null && library.getClosing() != null) {
            String isOpen;
            int color;
            if (library.isOpen()) {
                isOpen = "OPEN";
                color = ContextCompat.getColor(getApplicationContext(), R.color.green);
            } else {
                isOpen = "CLOSED";
                color = ContextCompat.getColor(getApplicationContext(), R.color.red);
            }

            String opening = HOURS_FORMAT.format(library.getOpening());
            String closing = HOURS_FORMAT.format(library.getClosing());
            hoursString = new SpannableString("Today  " + "                  " + isOpen + "\n" + "Hours ▼     " + opening + " to " + closing);
            hoursString.setSpan(new ForegroundColorSpan(color), 25, 25 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("Today ▼  CLOSED ALL DAY");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.maroon)), 9, 23, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return hoursString;
    }

    private Spanned setUpWeeklyHoursLeft() {
        /*
            This function is called with setUpWeeklyHoursRight() to set up the weekly hours
            page, which essentially appears as left and right justified text. The left side
            holds days of the week and some description.
         */
        Date[] openings = library.getWeeklyOpen();

        Spanned weeklyHoursString = new SpannableString("Today" + "\n" + "Hours ▲\n");
        for (int i=0; i < openings.length; i++) {
            Spannable hoursString;
            hoursString = new SpannableString("\n" + library.getDayOfWeek(i));
            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }

        return weeklyHoursString;
    }

    private Spanned setUpWeeklyHoursRight() {
        /*
            This function is called with setUpWeeklyHoursLeft() to set up the weekly hours
            page, which essentially appears as left and right justified text. The right side
            holds hours for each day of the week and some description.
         */
        Date[] openings = library.getWeeklyOpen();
        Date[] closings = library.getWeeklyClose();
        boolean[] byAppointments = library.getWeeklyAppointments();

        Spannable hoursString;
        if (library.isByAppointment()) {
            hoursString = new SpannableString("BY APPOINTMENT  \n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light )), 9, 23, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (library.getOpening() != null && library.getClosing() != null) {
            String isOpen;
            int color;
            if (library.isOpen()) {
                isOpen = "OPEN";
                color = ContextCompat.getColor(getApplicationContext(),R.color.green);
            } else {
                isOpen = "CLOSED";
                color = ContextCompat.getColor(getApplicationContext(),R.color.red);
            }

            hoursString = new SpannableString(isOpen + "\n" + "\n");
            hoursString.setSpan(new ForegroundColorSpan(color), 0,isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("CLOSED ALL DAY" + "\n" + "\n");
            hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, 15, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        Spanned weeklyHoursString = hoursString;
        for (int i=0; i < openings.length; i++) {
            if (byAppointments[i]) {
                hoursString = new SpannableString("\n  BY APPOINTMENT");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.pavan_light) ), 0, 16, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (openings[i] != null && closings[i] != null) {
                String opening = HOURS_FORMAT.format(openings[i]);
                String closing = HOURS_FORMAT.format(closings[i]);
                hoursString = new SpannableString("\n" + opening + " to " + closing);
                if (hoursString.length() > 20) {
                    hoursString = new SpannableString("\n" + opening + " - " + closing);
                }
            } else {
                hoursString = new SpannableString("\n" + "  CLOSED ALL DAY");
                hoursString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.maroon) ), 0, 17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            weeklyHoursString = (Spanned) TextUtils.concat(weeklyHoursString, hoursString);
        }

        return weeklyHoursString;
    }
}
