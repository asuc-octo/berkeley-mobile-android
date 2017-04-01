package com.asuc.asucmobile.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.ImageDownloadThread;
import com.asuc.asucmobile.utilities.SerializableUtilities;
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
import java.util.Locale;

public class OpenGymActivity extends BaseActivity {

    private static final LatLng CAMPUS_LOCATION = new LatLng(37.871899, -122.25854);
    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private static final int REQUEST_CODE_PLAY_SERVICES = 1;

    private Gym gym;
    private MapFragment mapFragment;
    private GoogleMap map;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open);
        exitIfNoData();
        setupToolbar(gym.getName(), true);

        // Populate UI.
        TextView nametag = (TextView) findViewById(R.id.nametag);
        TextView addresstag = (TextView) findViewById(R.id.addresstag);
        final TextView hours = (TextView) findViewById(R.id.hours);
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.progress_bar);
        final View image = findViewById(R.id.image);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        RelativeLayout phoneLayout = (RelativeLayout) findViewById(R.id.phone_layout);
        RelativeLayout locationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        Spannable hoursString;
        if (gym.getOpening() != null && gym.getClosing() != null) {
            String isOpen;
            int color;
            if (gym.isOpen()) {
                isOpen = "OPEN";
                color = Color.rgb(153, 204, 0);
            } else {
                isOpen = "CLOSED";
                color = Color.rgb(255, 68, 68);
            }
            String opening = HOURS_FORMAT.format(gym.getOpening());
            String closing = HOURS_FORMAT.format(gym.getClosing());
            hoursString = new SpannableString("Today  " + isOpen + "\n" + opening + " to " + closing);
            hoursString.setSpan(new ForegroundColorSpan(color), 7, 7 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("Today  UNKNOWN");
            hoursString.setSpan(new ForegroundColorSpan(Color.rgb(114, 205, 244)), 7, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        hours.setText(hoursString);
        nametag.setText(gym.getName());
        addresstag.setText(gym.getAddress());

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
                i.setData(Uri.parse("tel:"));
                startActivity(i);
            }
        });

        //Add in favoriting to the individual view.
        final Context context = getBaseContext();
        final ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(context);

        final ImageView iconFavorite = (ImageView) findViewById(R.id.icon_favorite);
        if (listOfFavorites != null && listOfFavorites.contains(gym.getName())) {
            iconFavorite.setImageResource(R.drawable.post_favorite);
        } else {
            iconFavorite.setImageResource(R.drawable.pre_favorite);
        }

        iconFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(gym.getName())) {
                    listOfFavorites.remove(gym.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    iconFavorite.setImageResource(R.drawable.pre_favorite);
                } else if (listOfFavorites != null){
                    listOfFavorites.add(gym.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    iconFavorite.setImageResource(R.drawable.post_favorite);
                }
            }
        });

        // Load gym image.
        loadingBar.bringToFront();
        new ImageDownloadThread(this, gym.getImageUrl(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data;
                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    image.setBackgroundDrawable(bitmapDrawable);
                }
                loadingBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRetrievalFailed() {
                image.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_gym));
                loadingBar.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
            }
        }).start();
        setUpMap();
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
                        if (gym.getCoordinates() != null) {
                            BitmapDescriptor bitmap =
                                    BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                            map.addMarker(new MarkerOptions()
                                    .position(gym.getCoordinates())
                                    .icon(bitmap)
                                    .title(gym.getName())
                            );
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(gym.getCoordinates(), 17));
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
        double lat = gym.getCoordinates().latitude;
        double lng = gym.getCoordinates().longitude;
        String uri = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?dirflg=w&saddr=Current+Location&daddr=%f,%f", lat, lng
        );
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
    }

    private void exitIfNoData() {
        gym = ((GymController) GymController.getInstance()).getCurrentGym();
        if (gym == null) {
            finish();
        }
    }

}
