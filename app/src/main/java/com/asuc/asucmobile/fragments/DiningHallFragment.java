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
import com.asuc.asucmobile.adapters.DiningHallAdapter;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.DiningHalls;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.models.Gyms;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class DiningHallFragment extends Fragment {

    private ListView mDiningList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private DiningHallAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dining_hall, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Dining Halls");
        setHasOptionsMenu(true);
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mDiningList = (ListView) layout.findViewById(R.id.dining_hall_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        mAdapter = new DiningHallAdapter(getContext());
        mDiningList.setAdapter(mAdapter);
        mDiningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiningController.setCurrentDiningHall(mAdapter.getItem(i));
                Intent intent = new Intent(getContext(), OpenDiningHallActivity.class);
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

    @Override
    @SuppressWarnings("deprecation")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining_hall, menu);
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

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the dining hall list
     * from the web.
     */
    private void refresh() {
        mDiningList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        DiningController.cService controller = Controller.retrofit.create(DiningController.cService.class);
        Call<DiningHalls> call = controller.getData();
        call.enqueue(new retrofit2.Callback<DiningHalls>() {
            @Override
            public void onResponse(Call<DiningHalls> call, Response<DiningHalls> response) {
                List<DiningHall> diningHalls = DiningController.parse(response.body(), getContext());
                mDiningList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.setList(diningHalls);
            }

            @Override
            public void onFailure(Call<DiningHalls> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
