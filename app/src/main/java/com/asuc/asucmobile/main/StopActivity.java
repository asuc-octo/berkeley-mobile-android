package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StopActivity extends Activity {

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

    private StopAdapter mAdapter;

    private double mlatitude = -1;
    private double mlongitude = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        // Get current location.
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        mAdapter = new StopAdapter(this, mlatitude, mlongitude);
        mDestList.setAdapter(mAdapter);

        mDestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mlatitude == -1 || mlongitude == -1) {
                    Toast.makeText(getBaseContext(), "Please turn on location to get directions.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Intent intent = new Intent(getBaseContext(), OpenRouteSelectionActivity.class);

                    intent.putExtra("stop_id", mAdapter.getItem(i).getId());
                    intent.putExtra("stop_name", mAdapter.getItem(i).getName());
                    intent.putExtra("lat", mlatitude);
                    intent.putExtra("long", mlongitude);

                    startActivity(intent);
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        refresh();
    }

    @Override
    protected void onStop() {
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