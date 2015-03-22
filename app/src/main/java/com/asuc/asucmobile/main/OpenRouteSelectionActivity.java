package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.RouteSelectionAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.controllers.RouteController;
import com.asuc.asucmobile.models.Route;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class OpenRouteSelectionActivity extends Activity {

    private ListView mTripList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private RouteController mController;
    private RouteSelectionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_route_selection);

        int stopId = getIntent().getIntExtra("stop_id", -1);
        String stopName = getIntent().getStringExtra("stop_name");
        double lat = getIntent().getDoubleExtra("lat", -1.0);
        double lng = getIntent().getDoubleExtra("long", -1.0);
        if (stopId == -1 || lat == -1.0 || lng == -1.0) {
            finish();
            return;
        }

        if (((LineController) (LineController.getInstance(this))).getStops().size() == 0) {
            finish();
            return;
        }

        ImageButton mRefreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);
        mTripList = (ListView) findViewById(R.id.tripList);

        Stop dest = ((LineController) (LineController.getInstance(this))).getStop(stopId, stopName);
        mController = (RouteController) RouteController.createInstance(this, new LatLng(lat, lng), dest.getLocation());

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        refresh();
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

    private void refresh() {
        mTripList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mController.refreshInBackground(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mTripList.setVisibility(View.VISIBLE);

                ArrayList<Route> routes = (ArrayList<Route>) data;

                if (routes.size() > 0) {
                    mAdapter = new RouteSelectionAdapter(getBaseContext(), routes);
                    mTripList.setAdapter(mAdapter);

                    mTripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mController.setCurrentRoute(mAdapter.getItem(position));
                            Intent intent = new Intent(getBaseContext(), OpenRouteActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    mTripList.setVisibility(View.INVISIBLE);
                }
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