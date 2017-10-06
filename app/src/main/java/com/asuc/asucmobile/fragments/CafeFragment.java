package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.asuc.asucmobile.adapters.CafeAdapter;
import com.asuc.asucmobile.adapters.DiningHallAdapter;
import com.asuc.asucmobile.controllers.CafeController;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;

import java.util.ArrayList;

/**
 * Created by rustie on 9/29/17.
 */

public class CafeFragment extends Fragment {
    
    private ListView mCafeList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private CafeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_hall, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Cafes");
        ImageButton refreshButton = (ImageButton) view.findViewById(R.id.refresh_button);
        mCafeList = (ListView) view.findViewById(R.id.dining_hall_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) view.findViewById(R.id.refresh);
        mAdapter = new CafeAdapter(getContext());
        mCafeList.setAdapter(mAdapter);
        mCafeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cafe cafe = mAdapter.getItem(i);
                CafeController controller = ((CafeController) CafeController.getInstance());
                controller.setCurrentCafe(cafe);
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


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the cafe list
     * from the web.
     */
    private void refresh() {
        mCafeList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        CafeController.getInstance().refreshInBackground(getActivity(), new Callback() {
            @Override
            public void onDataRetrieved(Object data) {
                mCafeList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.setList((ArrayList<Cafe>) data);
            }

            @Override
            public void onRetrievalFailed() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
