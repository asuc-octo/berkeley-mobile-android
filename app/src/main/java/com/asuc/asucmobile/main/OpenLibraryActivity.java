package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.asuc.asucmobile.models.Library;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OpenLibraryActivity extends Activity {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a");

    public static Library staticLibrary;

    private MapFragment mapFragment;
    private GoogleMap map;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "young.ttf");

        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(typeface);

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_open_library);

        library = staticLibrary;

        if (getActionBar() != null) {
            getActionBar().setTitle(library.getName());
        }

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView name = (TextView) findViewById(R.id.name);
        TextView hours = (TextView) findViewById(R.id.hours);
        TextView address = (TextView) findViewById(R.id.location);
        TextView phone = (TextView) findViewById(R.id.phone);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        name.setTypeface(typeface);
        hours.setTypeface(typeface);
        address.setTypeface(typeface);
        phone.setTypeface(typeface);

        Spannable hoursString;
        if (library.isByAppointment()) {
            hoursString = new SpannableString("Today  BY APPOINTMENT");
            hoursString.setSpan(new ForegroundColorSpan(Color.rgb(114, 205, 244)), 7, 21, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (library.getOpening() != null && library.getClosing() != null) {
            String isOpen;
            int color;
            if (library.isOpen()) {
                isOpen = "OPEN";
                color = Color.rgb(75, 220, 98);
            } else {
                isOpen = "CLOSED";
                color = Color.RED;
            }

            String opening = HOURS_FORMAT.format(library.getOpening());
            String closing = HOURS_FORMAT.format(library.getClosing());
            hoursString = new SpannableString("Today  " + isOpen + "\n" + opening + " to " + closing);
            hoursString.setSpan(new ForegroundColorSpan(color), 7, 7 + isOpen.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            hoursString = new SpannableString("Today  CLOSED ALL DAY");
            hoursString.setSpan(new ForegroundColorSpan(Color.RED), 7, 21, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        name.setText(library.getName());
        hours.setText(hoursString);
        address.setText(library.getLocation());
        phone.setText(library.getPhone());

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + library.getPhone()));
                startActivity(i);
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = sharedPref.getBoolean("first_time", true);
        if (firstTime) {
            Toast.makeText(this, "Click on the map for directions,\nor the phone to dial!", Toast.LENGTH_LONG).show();
            sharedPref.edit().putBoolean("first_time", false).apply();
        }

        setUpMap();
        new DownloadImageTask(image).execute(library.getImageUrl());
    }

    @Override
    public void onResume() {
        super.onResume();

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
                        double lat = library.getCoordinates().latitude;
                        double lng = library.getCoordinates().longitude;

                        String uri = String.format(
                                Locale.ENGLISH,
                                "http://maps.google.com/maps?saddr=Current+Location&daddr=%f,%f", lat, lng
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
