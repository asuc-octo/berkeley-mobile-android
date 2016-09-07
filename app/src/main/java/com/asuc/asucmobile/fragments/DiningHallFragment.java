package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.DiningHallAdapter;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiningHallFragment extends Fragment {

    private ListView mDiningList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private DiningHallAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FlurryAgent.onStartSession(getContext(), "4VPTT49FCCKH7Z2NVQ26");
        View layout = inflater.inflate(R.layout.fragment_dining_hall, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(toolbar);
        toolbar.setTitle("Dining Halls");

        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);

        mDiningList = (ListView) layout.findViewById(R.id.dining_hall_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);

        mAdapter = new DiningHallAdapter(getContext());
        mDiningList.setAdapter(mAdapter);

        mDiningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiningHall diningHall = mAdapter.getItem(i);
                DiningController controller = ((DiningController) DiningController.getInstance(getContext()));
                controller.setCurrentDiningHall(diningHall);
                Intent intent = new Intent(getContext(), OpenDiningHallActivity.class);

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

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(getContext());
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the dining hall list
     * from the web.
     */
    private void refresh() {
        mDiningList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        DiningController.getInstance(getContext()).refreshInBackground(new Callback() {
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
                Toast.makeText(getContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
