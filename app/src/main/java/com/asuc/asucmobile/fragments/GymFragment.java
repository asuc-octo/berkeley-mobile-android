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
import com.asuc.asucmobile.adapters.GymAdapter;
import com.asuc.asucmobile.main.OpenGymActivity;
import com.asuc.asucmobile.models.responses.GymClassesResponse;
import com.asuc.asucmobile.models.responses.GymsResponse;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GymFragment extends Fragment {

    private ListView mGymList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private GymAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    Call<GymsResponse> gymsCall;
    Call<GymClassesResponse> gymClassesCall;


    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_gym_screen", bundle);

        View layout = inflater.inflate(R.layout.fragment_gym, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Gyms");
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mGymList = (ListView) layout.findViewById(R.id.gym_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        mAdapter = new GymAdapter(getContext());
        mGymList.setAdapter(mAdapter);
        mGymList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenGymActivity.setGym(mAdapter.getItem(i));
                Intent intent = new Intent(getActivity(), OpenGymActivity.class);
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
        NavigationGenerator.closeMenu(getActivity());
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the gym list
     * from the web.
     */
    private void refresh() {

        gymsCall = BMRetrofitController.bmapi.callGymsList();
        gymClassesCall = BMRetrofitController.bmapi.callGymClasses();

        mGymList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        getGyms();
        getGymClasses();
    }

    private void getGyms() {

        gymsCall.enqueue(new retrofit2.Callback<GymsResponse>() {
            @Override
            public void onResponse(Call<GymsResponse> call, Response<GymsResponse> response) {
                mGymList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.setList(response.body().getGyms());
            }

            @Override
            public void onFailure(Call<GymsResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGymClasses(){
        gymClassesCall.enqueue(new Callback<GymClassesResponse>() {
            @Override
            public void onResponse(Call<GymClassesResponse> call, Response<GymClassesResponse> response) {
                GymClassesResponse g = response.body();
            }

            @Override
            public void onFailure(Call<GymClassesResponse> call, Throwable t) {

            }
        });
    }

}
