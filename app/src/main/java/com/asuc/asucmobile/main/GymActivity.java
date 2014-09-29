package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.GymAdapter;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;

import java.util.ArrayList;

public class GymActivity extends Activity {

    private ListView mGymList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private GymAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mGymList = (ListView) findViewById(R.id.gym_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        mAdapter = new GymAdapter(this);
        mGymList.setAdapter(mAdapter);

        mGymList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenGymActivity.staticGym = mAdapter.getItem(i);
                Intent intent = new Intent(getBaseContext(), OpenGymActivity.class);
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // TODO: Go back to previous activity.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the library list
     * from the web.
     */
    private void refresh() {
        mGymList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        GymController.getInstance(this).refreshInBackground(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mGymList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                mAdapter.setList((ArrayList<Gym>) data);
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
