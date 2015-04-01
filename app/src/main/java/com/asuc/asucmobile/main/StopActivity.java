package com.asuc.asucmobile.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.StopAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final Comparator<Stop> ALPHABETICAL_ORDER = new Comparator<Stop>() {
        public int compare(Stop stop1, Stop stop2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(stop1.getAbbreviatedName(), stop2.getAbbreviatedName());
            if (res == 0) {
                res = stop1.getAbbreviatedName().compareTo(stop2.getAbbreviatedName());
            }
            return res;
        }
    };

    private ListView mDestList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    GoogleApiClient mGoogleApiClient;

    private StopAdapter mAdapter;

    private double mLatitude = -1;
    private double mLongitude = -1;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");
        buildGoogleApiClient();
        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_stop);

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mDestList = (ListView) findViewById(R.id.stop_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        // Get location manager
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
//
//        // For quick access, retrieve the last location update
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        } else {
            mLatitude = -1;
            mLongitude = -1;
        }

        // Set up list
        mAdapter = new StopAdapter(this, mLatitude, mLongitude);
        mDestList.setAdapter(mAdapter);

        mDestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mLatitude == -1 || mLongitude == -1) {
                    Toast.makeText(getBaseContext(), "Retrieving location, make sure your location is enabled!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);

                    intent.putExtra("stop_id", mAdapter.getItem(i).getId());
                    intent.putExtra("stop_name", mAdapter.getItem(i).getName());
                    intent.putExtra("lat", mLatitude);
                    intent.putExtra("long", mLongitude);

                    startActivity(intent);
                }
            }
        });

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                mAdapter.setNewLocation(location);
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        refresh();
    }

    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
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
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

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

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
//        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
//            ((MainActivity)getActivity()).onDialogDismissed();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView search = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) search.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_action_search);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.destination, menu);

        final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flurry log for searching for something!
                FlurryAgent.logEvent("Tapped on the Search Button (Destinations)");
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Close the keyboard
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final Filter filter = mAdapter.getFilter();
                filter.filter(s);
                return true;
            }
        });

        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Flurry logging for pressing the Back Button
        FlurryAgent.logEvent("Tapped on the Back Button (Gyms)");
    }

    /**
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