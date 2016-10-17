package com.asuc.asucmobile.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.RouteSelectionAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.controllers.RouteController;
import com.asuc.asucmobile.models.Route;
import com.asuc.asucmobile.utilities.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

public class OpenRouteSelectionActivity extends BaseActivity {

    private ListView mTripList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private RouteController routeController;
    private LineController lineController;
    private RouteSelectionAdapter mAdapter;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_route_selection);
        setupToolbar("Select Route", true);

        // Get route parameters.
        LatLng startLatLng = getIntent().getParcelableExtra("startLngLat");
        LatLng endLatLng = getIntent().getParcelableExtra("endLngLat");
        Date departureTime = (Date) getIntent().getSerializableExtra("departureTime");

        // Populate UI.
        ImageButton mRefreshButton = (ImageButton) findViewById(R.id.refresh_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);
        mTripList = (ListView) findViewById(R.id.tripList);
        routeController = (RouteController) RouteController.createInstance(startLatLng, endLatLng, departureTime);
        lineController = (LineController) LineController.getInstance();
        if (mRefreshButton != null) {
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
        }
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.live_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.live_bus:
                Intent intent = new Intent(getBaseContext(), LiveBusActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void refresh() {
        mTripList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        lineController.refreshInBackground(this, new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                routeController.refreshInBackground(OpenRouteSelectionActivity.this, new Callback() {

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
                                    routeController.setCurrentRoute(mAdapter.getItem(position));
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

            @Override
            public void onRetrievalFailed() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
