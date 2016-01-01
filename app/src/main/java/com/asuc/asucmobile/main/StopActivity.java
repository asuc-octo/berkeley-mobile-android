package com.asuc.asucmobile.main;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final Comparator<Stop> ALPHABETICAL_ORDER = new Comparator<Stop>() {
        public int compare(Stop stop1, Stop stop2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(stop1.getAbbreviatedName(), stop2.getAbbreviatedName());
            if (res == 0) {
                res = stop1.getAbbreviatedName().compareTo(stop2.getAbbreviatedName());
            }
            return res;
        }
    };
    private static final int LOCATION_PERMISSION = 0;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private ListView mDestList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private GoogleApiClient mGoogleApiClient;
    private StopAdapter mAdapter;
    private double mLatitude = -1;
    private double mLongitude = -1;
    private int mRequestType;
    private boolean mResolvingError = false;

    // App lifecycle methods

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");
        buildGoogleApiClient();

        // Set up layout and toolbar
        setContentView(R.layout.activity_stop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get layout views
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        mDestList = (ListView) findViewById(R.id.stop_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        // Set up list
        mAdapter = new StopAdapter(this, mLatitude, mLongitude);
        mDestList.setAdapter(mAdapter);

        // Set up on click listeners for the stop list
        mDestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mLatitude == -1 || mLongitude == -1) {
                    Toast.makeText(getBaseContext(), "Retrieving location, make sure your location is enabled!", Toast.LENGTH_SHORT).show();
                } else {
                    Stop stop = mAdapter.getItem(i);
                    Intent intent = new Intent();
                    if(mRequestType == 1) {
                        intent.putExtra("startName", stop.getName());
                        intent.putExtra("startLatLng", stop.getLocation());
                    } else {
                        intent.putExtra("endName", stop.getName());
                        intent.putExtra("endLatLng", stop.getLocation());
                    }
                    setResult(mRequestType, intent);
                    finish();
                }
            }
        });

        mRequestType = getIntent().getIntExtra("requestCode", 1);

        // Set up on click listeners for the refresh button
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.destination, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
                // Setting up aesthetics
                EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(getResources().getColor(android.R.color.white));
                searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));

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
    public void onStart() {
        super.onStart();

        // Check if we're on Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCoarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFinePermission != PackageManager.PERMISSION_GRANTED && hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                // Ask for location permission
                String[] permissions = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION);
            } else {
                updateLocation();
            }
        } else {
            updateLocation();
        }

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        refresh();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Flurry logging for pressing the Back Button
        FlurryAgent.logEvent("Tapped on the Back Button (Stops)");
    }

    // Google Play Service callback methods

    @Override
    public void onConnected(Bundle connectionHint) {
        // Get Google's reported location ONLY if the location hasn't already been retreived
        if (mLatitude == -1 && mLongitude == -1) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                mAdapter.setNewLocation(mLastLocation);
            }
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
    public void onConnectionSuspended(int cause) {}

    // App event methods

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION && grantResults.length > 0 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            updateLocation();
        } else {
            Toast.makeText(this, "Please allow location permissions and try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Other methods

    private void updateLocation() {
        // Get location manager
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);

        try {
            // First try to get the last known location
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                mAdapter.setNewLocation(location);
            } else {
                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        mAdapter.setNewLocation(location);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                // Register the listener with the Location Manager to receive location updates
                try {
                    locationManager.requestSingleUpdate(bestProvider, locationListener, null);
                } catch (SecurityException e) {
                    // Do nothing
                }
            }
        } catch (SecurityException e) {
            finish();
        }
    }

    /*
     * refresh() updates the visibility of necessary UI elements and refreshes the library list
     * from the web.
     */
    private void refresh() {
        mDestList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        LineController.getInstance(this).refreshInBackground(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mDestList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                SparseArray<Stop> stopsMap = (SparseArray<Stop>) data;
                final ArrayList<Stop> stops = new ArrayList();
                for (int i = 0; i < stopsMap.size(); i++) {
                    stops.add(stopsMap.valueAt(i));
                }

                Collections.sort(stops, ALPHABETICAL_ORDER);

                mAdapter.setList(stops);
            }

            @Override
            public void onRetrievalFailed() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}