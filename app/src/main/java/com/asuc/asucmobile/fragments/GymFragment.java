package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.GroupExController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.main.OpenGymActivity;
import com.asuc.asucmobile.models.GroupExs;
import com.asuc.asucmobile.models.Gyms;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class GymFragment extends Fragment {

    private ListView mGymList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private GymAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_gym, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Gyms");
        setHasOptionsMenu(true);
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mGymList = (ListView) layout.findViewById(R.id.gym_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        mAdapter = new GymAdapter(getContext());
        mGymList.setAdapter(mAdapter);
        mGymList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GymController.setCurrentGym(mAdapter.getItem(i));
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

        mGymList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        refresh();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gym, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem m) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    return fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new ItemFragment())
                            .addToBackStack("tag")
                            .commit() > 0;
                }
            });

        }
    }

    public void refresh() {
        GymController.cService controller = Controller.retrofit.create(GymController.cService.class);
        Call<Gyms> call = controller.getGyms();
        call.enqueue(new retrofit2.Callback<Gyms>() {
            @Override
            public void onResponse(Call<Gyms> call, Response<Gyms> response) {
                if (response.isSuccessful()) {
                    mGymList.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    mAdapter.setList(GymController.parse(response.body()));
                } else {
                    onFailure(null, null);
                }
            }

            @Override
            public void onFailure(Call<Gyms> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
