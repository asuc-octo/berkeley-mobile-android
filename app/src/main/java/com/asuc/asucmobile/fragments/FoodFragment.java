package com.asuc.asucmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.controllers.CafeController;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodPlace;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;

import java.util.ArrayList;

/**
 * Created by rustie on 10/4/17.
 */

public class FoodFragment extends Fragment {

    public static final String TAG = "FoodFragment";

    private TextView mDiningHallLabel;
    private TextView mCafeLabel;

    private RecyclerView mDiningRecyclerView;
    private RecyclerView mCafeRecyclerView;

    private ArrayList<FoodPlace> mDiningHallList;
    private ArrayList<FoodPlace> mCafeList;
    
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;


    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_food, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Food");
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);


        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);

        mDiningHallLabel = (TextView) layout.findViewById(R.id.dining_halls_label);
        mCafeLabel = (TextView) layout.findViewById(R.id.cafes_label);


        mDiningRecyclerView = (RecyclerView) layout.findViewById(R.id.dining_hall_recycler_view);
        mDiningRecyclerView.setHasFixedSize(true);
        mDiningHallList = new ArrayList<>();
        mDiningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

        // null check on recyclerviews and size check on lists


        mCafeRecyclerView = (RecyclerView) layout.findViewById(R.id.cafe_recycler_view);
        mCafeRecyclerView.setHasFixedSize(true);
        mCafeList = new ArrayList<>();
        mCafeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));


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
        NavigationGenerator.closeMenu(getActivity());
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the dining hall list
     * from the web.
     */
    private void refresh() {
        mCafeLabel.setVisibility(View.GONE);
        mDiningHallLabel.setVisibility(View.GONE);

        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mCafeRecyclerView.setVisibility(View.GONE);
        mDiningRecyclerView.setVisibility(View.GONE);

        getDining();
        getCafes();

    }

    private void getCafes() {
        CafeController.getInstance().refreshInBackground(getActivity(), new Callback() {

            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mCafeLabel.setVisibility(View.VISIBLE);
                mCafeRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mCafeList= (ArrayList<FoodPlace>) data;
                mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));
                Log.d(TAG, data.toString());


            }

            @Override
            public void onRetrievalFailed() {

                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Unable to retrieve cafe data please try again",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getDining() {
        DiningController.getInstance().refreshInBackground(getActivity(), new Callback() {

            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mDiningHallLabel.setVisibility(View.VISIBLE);
                mDiningRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mDiningHallList = (ArrayList<FoodPlace>) data;
                mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

                Log.d(TAG, data.toString());

            }

            @Override
            public void onRetrievalFailed() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve dining hall data, please try again",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

}
