package com.asuc.asucmobile.main;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Filter;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class ListStartStopActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private StopAdapter mAdapter;

    private double mLatitude = -1;
    private double mLongitude = -1;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_start_stop);
        Intent parentIntent = getIntent();
        buildGoogleApiClient();
        // Get location manager
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);

        // For quick access, retrieve the last location update
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        } else {
            mLatitude = -1;
            mLongitude = -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.destination, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // Close the keyboard
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        final Filter filter = mAdapter.getFilter();
                        filter.filter(s);
                        return true;
                    }
                });
            }
        }

        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            mAdapter.setNewLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
