package com.asuc.asucmobile.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.asuc.asucmobile.utilities.LocationGrabber;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopActivity extends AppCompatActivity {

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

    private ListView mDestList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private StopAdapter mAdapter;
    private int mRequestType;

    ///////////////////////////////////////
    //////// App lifecycle methods ////////
    ///////////////////////////////////////

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

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
        mAdapter = new StopAdapter(this);
        mDestList.setAdapter(mAdapter);

        // Set up on click listeners for the stop list
        mDestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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

        LocationGrabber.getLocation(this, new LocationCallback());
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Flurry logging for pressing the Back Button
        FlurryAgent.logEvent("Tapped on the Back Button (Stops)");
    }

    //////////////////////////////////////
    //////// App callback methods ////////
    //////////////////////////////////////

    /**
     * onRequestPermissionsResult() is called from LocationManager when it requests location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION && grantResults.length > 0 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            LocationGrabber.getLocation(this, new LocationCallback());
        } else {
            Toast.makeText(this, "Please allow location permissions and try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    ///////////////////////////////
    //////// Miscellaneous ////////
    ///////////////////////////////

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

    private class LocationCallback implements Callback {

        @Override
        public void onDataRetrieved(Object data) {
            LatLng latLng = (LatLng) data;
            mAdapter.setNewLocation(latLng);
        }

        @Override
        public void onRetrievalFailed() {}

    }

}