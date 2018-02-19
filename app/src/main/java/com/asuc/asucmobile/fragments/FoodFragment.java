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
import com.asuc.asucmobile.controllers.BMAPI;
import com.asuc.asucmobile.models.responses.CafesResponse;
import com.asuc.asucmobile.models.responses.DiningHallsResponse;
import com.asuc.asucmobile.models.FoodPlace;
import com.asuc.asucmobile.singletons.BMRetrofitController;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rustie on 10/4/17.
 */

public class FoodFragment extends Fragment {

    public static final String TAG = "FoodFragment";

    private TextView mDiningHallLabel;
    private TextView mCafeLabel;

    private RecyclerView mDiningRecyclerView;
    private RecyclerView mCafeRecyclerView;

    private List<FoodPlace> mDiningHallList;
    private List<FoodPlace> mCafeList;
    
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private FirebaseAnalytics mFirebaseAnalytics;

    Call<DiningHallsResponse> diningHallsCall;
    Call<CafesResponse> cafesCall;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_food, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Food");
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_food_screen", bundle);

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

        diningHallsCall = BMRetrofitController.bmapi.callDiningHallList();
        cafesCall = BMRetrofitController.bmapi.callCafeList();

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

        cafesCall.enqueue(new Callback<CafesResponse>() {
            @Override
            public void onResponse(Call<CafesResponse> call, Response<CafesResponse> response) {


                List<FoodPlace> dh = (List<FoodPlace>) response.body().getCafes();
                mCafeList = dh;

                mCafeLabel.setVisibility(View.VISIBLE);
                mCafeRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));
                Log.d(TAG, dh.toString());
            }

            @Override
            public void onFailure(Call<CafesResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve cafe data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDining() {

        diningHallsCall.enqueue(new retrofit2.Callback<DiningHallsResponse>() {
             @Override
             public void onResponse(Call<DiningHallsResponse> call, Response<DiningHallsResponse> response) {

                 Log.d("Dining Response: ", response.headers().toString());
                 Log.d("Dining Response: ", response.body().toString());

                 if (response.raw().cacheResponse() != null) {
                     // true: response was served from cache
                     Log.d("Dining Response: ", "Served from Cache");

                 }

                 if (response.raw().networkResponse() != null) {
                     // true: response was served from network/server
                     Log.d("Dining Response: ", "Served from Network");

                 }

                 if (BMRetrofitController.isConnected()) {
                     Log.d("Dining Response: ", "Connected");
                 }



                 List<FoodPlace> dh = (List<FoodPlace>) response.body().getDiningHalls();
                mDiningHallList = dh;

                mDiningHallLabel.setVisibility(View.VISIBLE);
                mDiningRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));
                 Log.d(TAG, dh.toString());
             }

             @Override
             public void onFailure(Call<DiningHallsResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve dining hall data, please try again",
                        Toast.LENGTH_SHORT).show();
             }
         });
    }

}
