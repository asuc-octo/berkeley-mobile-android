package com.asuc.asucmobile.domain.main;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.MyFragmentPageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexthomas on 4/9/17.
 * Modified by sidthesquid 3/18
 */

public class PopUpActivity extends DialogFragment {
    MyFragmentPageAdapter mPageAdapter;
    private ArrayList<String> busRoutes = new ArrayList<>();
    ProgressBar progressBar;
    RelativeLayout refreshWrapper;
    ViewPager viewPager;
    private TextView markerTitle, desc1View, desc2View, dist;
    private ImageView icon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_bus_info, container, false);
//        adjustScreen(0.83, 0.18); // Makes the activity a small window
        //Instantiates all UI elements
        List<Fragment> fragmentList = new ArrayList<>();
        markerTitle = (TextView) v.findViewById(R.id.mainName);
        desc1View = (TextView) v.findViewById(R.id.desc1);
        desc2View = (TextView) v.findViewById(R.id.desc2);
        dist = (TextView) v.findViewById(R.id.dist);


        //Attaches the tab dots
      /*  TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);*/
        String category = getArguments().getString("title");
        String desc1 = getArguments().getString("desc1");
        String desc2 = getArguments().getString("desc2");
        String distance = getArguments().getString("distance");

        icon = (ImageView) v.findViewById(R.id.icon);

        markerTitle.setText(category);
        desc1View.setText(desc1);
        desc2View.setText(desc2);
        dist.setText(distance);

        Log.d("Marker Title: ", category);

        switch (category){
            case "microwave":
                icon.setImageResource(R.drawable.microwave_map_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
            case "Water Fountain":
                icon.setImageResource(R.drawable.waterbottle_map_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.aqua));
                break;
            case "Nap Pod":
                icon.setImageResource(R.drawable.sleeppod_map_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.pink));
                break;
            case "Ford Go Bike":
                icon.setImageResource(R.drawable.bike_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.bike_blue));
                break;
            case "Printer":
                icon.setImageResource(R.drawable.printer_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.printer_green));
                break;
            case "Mental Health":
                icon.setImageResource(R.drawable.mental_health_icon);
                markerTitle.setTextColor(getResources().getColor(R.color.mental_health_yellow));
                break;
        }

        return v;

    }

    private void refresh() {
        //progressBar.setVisibility(View.VISIBLE);
        //refreshWrapper.setVisibility(View.GONE);
//        final String title = getIntent().getStringExtra("title");
//        final String id = getIntent().getStringExtra("id");
//        final String desc1 = getIntent().getStringExtra("desc1");


     /*   BusController.getInstance(id, title).refreshInBackground(this, new Callback() {
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


                    ArrayList<String> routeNames = new ArrayList<>();

                    for (BusInfo busInfo : busInfos) {
                        Bundle args = new Bundle();
                        args.putString("title", busInfo.getTitle());
                        args.putIntegerArrayList("nearTimes", busInfo.getNearTimes());
                        args.putString("nearestTime", busInfo.getNearestTime());
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
        });*/


    }

//
//    private void adjustScreen(double fractWidth, double fractHeight) {
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * fractWidth), (int) (height * fractHeight));
//    }


    public void addFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        mPageAdapter.add(fragment);
        mPageAdapter.notifyDataSetChanged();
    }


}