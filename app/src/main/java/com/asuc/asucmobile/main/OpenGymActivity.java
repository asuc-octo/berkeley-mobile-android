package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OpenGymActivity extends Activity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a");

    private MapFragment mapFragment;
    private GoogleMap map;
    private Gym gym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gym = ((GymController) GymController.getInstance(this)).getCurrentGym();
        if (gym == null) {
            finish();
            return;
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "young.ttf");

        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(typeface);

            getActionBar().setTitle(gym.getName());
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_open_facility);

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        View bottomDivider = findViewById(R.id.bottom_divider);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        hours.setTypeface(typeface);
        address.setTypeface(typeface);

        phoneLayout.setVisibility(View.GONE);
        bottomDivider.setVisibility(View.GONE);

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
        address.setText(gym.getAddress());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = sharedPref.getBoolean("gym_first_open", true);
        if (firstTime) {
            Toast.makeText(this, "Click on the map for directions!", Toast.LENGTH_LONG).show();
            sharedPref.edit().putBoolean("gym_first_open", false).apply();
        }

        setUpMap();
        new DownloadImageTask(image).execute(gym.getImageUrl());
    }

    @Override
    public void onResume() {
        super.onResume();

        gym = ((GymController) GymController.getInstance(this)).getCurrentGym();
        if (gym == null) {
            finish();
        }

        setUpMap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMap() {
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                List<Address> addresses;

                try {
                    Geocoder geocoder = new Geocoder(this);
                    addresses = geocoder.getFromLocationName(gym.getAddress(), 1);
                    if (addresses != null) {
                        addresses = geocoder.getFromLocationName(gym.getName(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                LatLng latLng;

                if (addresses == null) {
                    return;
                } else {
                    Address address = addresses.get(0);
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                }

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin);
                map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(bitmap)
                                .title(gym.getName())
                );
                map.getUiSettings().setZoomControlsEnabled(false);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        double lat = latLng.latitude;
                        double lng = latLng.longitude;

                        String uri = String.format(
                                Locale.ENGLISH,
                                "http://maps.google.com/maps?dirflg=w&saddr=Current+Location&daddr=%f,%f", lat, lng
                        );

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(i);
                    }
                });
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView image;

        public DownloadImageTask(ImageView image) {
            this.image = image;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;

            try {
                InputStream input = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                image.setImageBitmap(result);
            }
        }

    }

}
