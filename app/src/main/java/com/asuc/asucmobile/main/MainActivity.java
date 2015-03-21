package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.controllers.LineController;
import com.asuc.asucmobile.controllers.StopController;
import com.asuc.asucmobile.models.Category;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        }
        setContentView(R.layout.activity_main);

        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.dining_hall_blue), "DINING HALL MENUS", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.library_blue), "LIBRARY HOURS", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.gym_blue), "GYM HOURS", new Intent(this, GymActivity.class)),
                new Category(getResources().getDrawable(R.drawable.transit_blue), "BEARTRANSIT", new Intent(this, OpenRouteSelectionActivity.class))
        };

        ListView menuList = (ListView) findViewById(R.id.main_menu);

        final MainMenuAdapter adapter = new MainMenuAdapter(this, menuItems);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 3) {
                    final Intent intent = adapter.getItem(i).getIntent();
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    String bestProvider = locationManager.getBestProvider(new Criteria(), false);
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    try {
                        final double lat = location.getLatitude();
                        final double lng = location.getLongitude();

                        StopController.getInstance(MainActivity.this).refreshInBackground(new Callback() {
                            @Override
                            public void onDataRetrieved(Object data) {
                                final SparseArray<Stop> stops = (SparseArray<Stop>) data;
                                LineController.getInstance(MainActivity.this).refreshInBackground(new Callback() {
                                    @Override
                                    public void onDataRetrieved(Object data) {
                                        intent.putExtra("stop_id", stops.valueAt(0).getId());
                                        intent.putExtra("lat", lat);
                                        intent.putExtra("long", lng);

                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onRetrievalFailed() {

                                    }
                                });
                            }

                            @Override
                            public void onRetrievalFailed() {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(adapter.getItem(i).getIntent());
                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
