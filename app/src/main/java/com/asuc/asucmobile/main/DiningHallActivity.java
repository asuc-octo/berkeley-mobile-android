package com.asuc.asucmobile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.DiningHallAdapter;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.models.Category;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiningHallActivity extends AppCompatActivity {

    private ListView mDiningList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private DiningHallAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        setContentView(R.layout.activity_dining_hall);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.beartransit), "BearTransit", new Intent(this, StartStopSelectActivity.class)),
                new Category(getResources().getDrawable(R.drawable.dining_hall), "Dining Halls", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.library), "Libraries", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.gym), "Gyms", new Intent(this, GymActivity.class))
        };

        final MainMenuAdapter adapter = new MainMenuAdapter(this, menuItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(adapter.getItem(i).getIntent());
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mDiningList = (ListView) findViewById(R.id.dining_hall_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        mAdapter = new DiningHallAdapter(this);
        mDiningList.setAdapter(mAdapter);

        mDiningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiningController controller = ((DiningController) DiningController.getInstance(getBaseContext()));
                controller.setCurrentDiningHall(mAdapter.getItem(i));
                Intent intent = new Intent(getBaseContext(), OpenDiningHallActivity.class);

                //Flurry log for tapping Dining Hall Menus.
                Map<String, String> diningParams = new HashMap<>();
                diningParams.put("Hall", mAdapter.getItem(i).getName());
                FlurryAgent.logEvent("Taps Dining Hall Menus", diningParams);

                startActivity(intent);
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
        FlurryAgent.logEvent("Tapped on the Back Button (Dining Halls)");
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the dining hall list
     * from the web.
     */
    private void refresh() {
        mDiningList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        DiningController.getInstance(this).refreshInBackground(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mDiningList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                mAdapter.setList((ArrayList<DiningHall>) data);
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
