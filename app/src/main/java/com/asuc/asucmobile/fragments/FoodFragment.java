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

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.controllers.BMAPI;
import com.asuc.asucmobile.dagger.components.DaggerRetrofitComponent;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.models.responses.CafesResponse;
import com.asuc.asucmobile.models.responses.DiningHallsResponse;
import com.asuc.asucmobile.models.FoodPlace;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.ServerUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rustie on 10/4/17.
 */

public class FoodFragment extends Fragment {

    public static final String TAG = "FoodFragment";

    @Inject BMAPI bmapi;

    private TextView mDiningHallLabel;
    private TextView mCafeLabel;

    private RecyclerView mDiningRecyclerView;
    private RecyclerView mCafeRecyclerView;

    private List<FoodPlace> mDiningHallList;
    private List<FoodPlace> mCafeList;

    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Call<DiningHallsResponse> mDiningHallsCall;
    private Call<CafesResponse> mCafesCall;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GlobalApplication.getRetrofitComponent().inject(this);

        View layout = inflater.inflate(R.layout.fragment_food, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Food");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_food_screen", bundle);

        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);

        mDiningHallLabel = (TextView) layout.findViewById(R.id.dining_halls_label);
        mCafeLabel = (TextView) layout.findViewById(R.id.cafes_label);

        // set up dining hall RecyclerView
        mDiningRecyclerView = (RecyclerView) layout.findViewById(R.id.dining_hall_recycler_view);
        mDiningRecyclerView.setHasFixedSize(true);
        mDiningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDiningHallList = new ArrayList<>();
        mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

        // set up cafe RecyclerView
        mCafeRecyclerView = (RecyclerView) layout.findViewById(R.id.cafe_recycler_view);
        mCafeRecyclerView.setHasFixedSize(true);
        mCafeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mCafeList = new ArrayList<>();
        mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));

        // set up refresh
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        refresh(); // fetch data
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

        mDiningHallsCall = bmapi.callDiningHallList();
        mCafesCall = bmapi.callCafeList();

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

        mCafesCall.enqueue(new Callback<CafesResponse>() {
            @Override
            public void onResponse(Call<CafesResponse> call, Response<CafesResponse> response) {


                List<FoodPlace> dh = (List<FoodPlace>) response.body().getCafes();
                for (FoodPlace cafe : dh) {
                    ((Cafe) cafe).setMeals();
                }
                mCafeList = dh;

                mCafeLabel.setVisibility(View.VISIBLE);
                mCafeRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));
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

        mDiningHallsCall.enqueue(new retrofit2.Callback<DiningHallsResponse>() {
            @Override
            public void onResponse(Call<DiningHallsResponse> call, Response<DiningHallsResponse> response) {

                mDiningHallList = (List<FoodPlace>) response.body().getDiningHalls();

                mDiningHallLabel.setVisibility(View.VISIBLE);
                mDiningRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

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