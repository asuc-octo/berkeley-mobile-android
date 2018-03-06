package com.asuc.asucmobile.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MyFragmentPageAdapter;
import com.asuc.asucmobile.controllers.BusController;
import com.asuc.asucmobile.fragments.BusFailedFragment;
import com.asuc.asucmobile.fragments.BusInfoFragment;
import com.asuc.asucmobile.models.BusInfo;
import com.asuc.asucmobile.utilities.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexthomas on 4/9/17.
 */

public class PopUpActivity extends FragmentActivity {
    MyFragmentPageAdapter mPageAdapter;
    private ArrayList<String> busRoutes = new ArrayList<>();
    ProgressBar progressBar;
    RelativeLayout refreshWrapper;
    ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start_stop_select);
        adjustScreen(0.73, 0.18); // Makes the activity a small window
        //Instantiates all UI elements
        List<Fragment> fragmentList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mPageAdapter = new MyFragmentPageAdapter(this, getSupportFragmentManager(), fragmentList);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        refreshWrapper = (RelativeLayout) findViewById(R.id.refreshWrapper);
        viewPager.setAdapter(mPageAdapter);

        //Attaches the tab dots
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);


        refresh();

    }

    private void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        refreshWrapper.setVisibility(View.GONE);
        final String title = getIntent().getStringExtra("title");
        final String id = getIntent().getStringExtra("id");


        BusController.getInstance(id, title).refreshInBackground(this, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @SuppressWarnings("unchecked")
            @Override
            public void onDataRetrieved(Object data) {
                progressBar.setVisibility(View.GONE);
                refreshWrapper.setVisibility(View.VISIBLE);
                ArrayList<BusInfo> busInfos = (ArrayList<BusInfo>) data;
                if (busInfos.size() == 0) {
                    onRetrievalFailed();
                } else {

                    String distance = getIntent().getStringExtra("distance") + " miles away";
                    ArrayList<String> routeNames = new ArrayList<>();

                    for (BusInfo busInfo : busInfos) {
                        Bundle args = new Bundle();
                        args.putString("title", busInfo.getTitle());
                        args.putIntegerArrayList("nearTimes", busInfo.getNearTimes());
                        args.putString("nearestTime", busInfo.getNearestTime());
                        args.putString("distance", distance);
                        args.putString("routeName", busInfo.getRouteName());
                        args.putString("directionName", busInfo.getRouteBusTitle());
                        addFragment(new BusInfoFragment(), args);

                    }
                }


            }

            @Override
            public void onRetrievalFailed() {
                progressBar.setVisibility(View.GONE);
                refreshWrapper.setVisibility(View.VISIBLE);
                Bundle args = new Bundle();
                args.putString("title", title);
                addFragment(new BusFailedFragment(), args);
                //Toast.makeText(getBaseContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void adjustScreen(double fractWidth, double fractHeight) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * fractWidth), (int) (height * fractHeight));
    }


    public void addFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        mPageAdapter.add(fragment);
        mPageAdapter.notifyDataSetChanged();
    }


}