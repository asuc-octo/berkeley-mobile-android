package com.asuc.asucmobile.domain.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.asuc.asucmobile.domain.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.responses.CafesResponse;
import com.asuc.asucmobile.domain.models.responses.DiningHallsResponse;
import com.asuc.asucmobile.domain.models.FoodPlace;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

/**
 * Created by rustie on 10/4/17.
 */

public class FoodFragment extends Fragment {

    public static final String TAG = "FoodFragment";

    @Inject
    Repository<DiningHall> diningHallRepository;

    @Inject
    Repository<Cafe> cafeRepository;

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

    private boolean diningHidden;
    private boolean cafeHidden;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GlobalApplication.getRepositoryComponent().inject(this);

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
        mProgressBar.setVisibility(View.VISIBLE);

        diningHidden = cafeHidden = false;

        mDiningHallLabel = (TextView) layout.findViewById(R.id.dining_halls_label);
        mCafeLabel = (TextView) layout.findViewById(R.id.cafes_label);

        mDiningHallLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
        mCafeLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);

        // set up dining hall RecyclerView
        mDiningRecyclerView = (RecyclerView) layout.findViewById(R.id.dining_hall_recycler_view);
        mDiningRecyclerView.setNestedScrollingEnabled(false);
        mDiningRecyclerView.setHasFixedSize(true);
        mDiningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDiningHallList = new ArrayList<>();
        mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

        // set up cafe RecyclerView
        mCafeRecyclerView = (RecyclerView) layout.findViewById(R.id.cafe_recycler_view);
        mCafeRecyclerView.setNestedScrollingEnabled(false);
        mCafeRecyclerView.setHasFixedSize(true);
        mCafeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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

        mCafeLabel.setVisibility(View.GONE);
        mDiningHallLabel.setVisibility(View.GONE);

        mRefreshWrapper.setVisibility(View.GONE);

        mCafeRecyclerView.setVisibility(View.GONE);
        mDiningRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        getDining();
        getCafes();

    }

    private void getCafes() {

        cafeRepository.scanAll((List<Cafe>) (List<?>) mCafeList, new RepositoryCallback<Cafe>() {
            @Override
            public void onSuccess() {

                mCafeLabel.setVisibility(View.VISIBLE);
                mCafeRecyclerView.setVisibility(View.VISIBLE);
                mCafeLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cafeHidden) {
                            mCafeRecyclerView.setVisibility(View.VISIBLE);
                            mCafeLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
                        } else {
                            mCafeRecyclerView.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            mCafeLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
                        }
                        cafeHidden = !cafeHidden;
                    }
                });
                mProgressBar.setVisibility(View.GONE);
                mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));

            }

            @Override
            public void onFailure() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve cafe data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDining() {

        diningHallRepository.scanAll((List<DiningHall>) (List<?>) mDiningHallList, new RepositoryCallback<DiningHall>() {
            @Override
            public void onSuccess() {
                mDiningHallLabel.setVisibility(View.VISIBLE);
                //hide until clicked
                mDiningRecyclerView.setVisibility(View.VISIBLE);
                mDiningHallLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (diningHidden) {
                            mDiningRecyclerView.setVisibility(View.VISIBLE);
                            mDiningHallLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);

                        } else {
                            mDiningRecyclerView.setVisibility(View.GONE);
                            mDiningHallLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
                        }
                        diningHidden = !diningHidden;
                    }
                });
                mProgressBar.setVisibility(View.GONE);
                mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));
            }

            @Override
            public void onFailure() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve dining hall data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}