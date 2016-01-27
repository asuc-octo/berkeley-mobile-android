package com.asuc.asucmobile.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.HamburgerGenerator;
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
        HamburgerGenerator.generateMenu(this);
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

        final ImageView image = (ImageView) findViewById(R.id.image);
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        final ProgressBar progressBar= (ProgressBar) findViewById(R.id.progress_bar);

        Spannable hoursString;
        if (library.isByAppointment()) {
            hoursString = new SpannableString("Today  BY APPOINTMENT");
            hoursString.setSpan(new ForegroundColorSpan(Color.rgb(114, 205, 244)), 7, 21, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (library.getOpening() != null && library.getClosing() != null) {
            String isOpen;
            int color;
            if (library.isOpen()) {
                isOpen = "OPEN";
                color = Color.rgb(153, 204, 0);
            } else {
                isOpen = "CLOSED";
                color = Color.rgb(255, 68, 68);
            }

            String opening = HOURS_FORMAT.format(library.getOpening());
            String closing = HOURS_FORMAT.format(library.getClosing());
            hoursString = new SpannableString("Today  " + isOpen + "\n" + opening + " to " + closing);
            hoursString.setSpan(new ForegroundColorSpan(color), 7, 7 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("Today  CLOSED ALL DAY");
            hoursString.setSpan(new ForegroundColorSpan(Color.rgb(186, 52, 52)), 7, 21, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        hours.setText(hoursString);
        address.setText(library.getLocation());
        phone.setText(library.getPhone());

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
        new ImageDownloadThread(this, library.getImageUrl(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data;
                    image.setImageBitmap(bitmap);
                }

                progressBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRetrievalFailed() {
                progressBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }
        }).start();
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

}
