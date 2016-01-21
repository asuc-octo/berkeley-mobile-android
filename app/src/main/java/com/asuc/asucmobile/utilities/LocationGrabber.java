package com.asuc.asucmobile.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

public class LocationGrabber {

    private static final int LOCATION_PERMISSION = 0;

    /**
     * getLocation() is the public entrypoint to update location and handle it in the callback.
     */
    public static void getLocation(final Activity activity, final Callback callback) {
        // Check if we're on Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permissions
            int hasCoarsePermission =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasFinePermission =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

            // Ask for location permissions if we don't already have them
            if (hasFinePermission != PackageManager.PERMISSION_GRANTED &&
                    hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION);
            }
        }

        // Run a new background thread to grab location
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateLocation(activity, callback);
            }
        }).start();
    }

    /**
     * updateLocation() tries to get the last known location first (through Google Play Services and
     * Android APIs), then tries to manually grab location as a fallback.
     */
    private static void updateLocation(final Activity activity, final Callback callback) {
        Location location;

        // Get location manager
        final android.location.LocationManager locationManager =
                (android.location.LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final String bestProvider = locationManager.getBestProvider(new Criteria(), false);

        try {
            // Build the GoogleApiClient to get last known location
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .build();

            // Connect to GoogleApiClient
            ConnectionResult result = googleApiClient.blockingConnect(3000, TimeUnit.MILLISECONDS);

            // Check if GoogleApiClient has a location for us
            if (result.isSuccess()) {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (location != null) {
                    returnLocation(activity, callback, location.getLatitude(), location.getLongitude());
                    googleApiClient.disconnect();
                    return;
                }
                googleApiClient.disconnect();
            }

            // If GoogleApiClient failed, try to get the last known location from Android's API
            location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                returnLocation(activity, callback, location.getLatitude(), location.getLongitude());
                return;
            }

            // If all else fails, use our fallback and manually request a location update
            final LocationListener locationListener = new LocationListener() {

                // Called when a new location is found by the network location provider.
                public void onLocationChanged(Location location) {
                    returnLocation(activity, callback, location.getLatitude(), location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {
                    callback.onRetrievalFailed();
                }

            };

            // Register the listener with the Location Manager to receive location updates
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        locationManager.requestSingleUpdate(bestProvider, locationListener, null);
                    } catch (SecurityException e) {
                        callback.onRetrievalFailed();
                    }
                }
            });
        } catch (SecurityException e) {
            callback.onRetrievalFailed();
        }
    }

    /**
     * returnLocation() takes cares of returning the location back through the callback while also
     * making sure it is ran on the main UI thread.
     */
    private static void returnLocation(Activity activity, final Callback callback, final double lat, final double lng) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onDataRetrieved(new LatLng(lat, lng));
            }
        });
    }

}
