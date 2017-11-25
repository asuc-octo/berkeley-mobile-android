package com.asuc.asucmobile.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.RouteSelectionAdapter;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexthomas on 10/14/17.
 */

public class RouteSelectActivity extends BaseActivity {


    private ListView busList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_list_times);

        setupToolbar("Routes", true);
        ArrayList<Journey> routes = (ArrayList<Journey>) getIntent().getSerializableExtra("routes");

        final RouteSelectionAdapter routeAdapter = new RouteSelectionAdapter(getBaseContext(), routes);
        busList = (ListView) findViewById(R.id.routeList);
        busList.setAdapter(routeAdapter);

        busList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Journey journey = routeAdapter.getItem(position);
                ArrayList<Trip> trips = journey.getTrips();
                Intent busTimes = new Intent(RouteSelectActivity.this, RouteSelected.class);
                busTimes.putExtra("list_of_times", journey);
                startActivity(busTimes);
            }
        });
    }


}


