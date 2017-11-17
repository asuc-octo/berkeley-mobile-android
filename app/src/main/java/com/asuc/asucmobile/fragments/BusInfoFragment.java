package com.asuc.asucmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.asuc.asucmobile.R;

import java.util.ArrayList;


/**
 * Created by alexthomas on 4/16/17.
 */

public class BusInfoFragment extends Fragment {
    private TextView markerTitle, mainTime, mainTime1, mainTime2, min1, min2, routeName, direction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bus_info, container,
                false);

        //Instantiates items
        markerTitle = (TextView) view.findViewById(R.id.busStopName);
        routeName = (TextView) view.findViewById(R.id.routeName);
        mainTime = (TextView) view.findViewById(R.id.mainTime);
        mainTime1 = (TextView) view.findViewById(R.id.mainTime1);
        mainTime2 = (TextView) view.findViewById(R.id.mainTime2);
        direction = (TextView) view.findViewById(R.id.directionName);
        min1 = (TextView) view.findViewById(R.id.min1);
        min2 = (TextView) view.findViewById(R.id.min2);

        getArguments().getString("nearTimes");


        //Sets popUp title to marker
        markerTitle.setText(getArguments().getString("title"));
        direction.setText(getArguments().getString("directionName"));
        if (getArguments().getString("distance") != null
                || getArguments().getString("nearestTime") != null
                || getArguments().getString("nearTimes") != null || getArguments().getString("nearTimes") != null) {
            //estimatedArrivals.setText(getArguments().getString("nearestTime"));
            mainTime1.setText(getArguments().getString("nearTimes"));
            routeName.setText(getArguments().getString("routeName"));
            ArrayList<Integer> minutes = getArguments().getIntegerArrayList("nearTimes");


            if (minutes.size() == 1) {
                mainTime.setText(String.valueOf(minutes.get(0)));
                mainTime1.setText("");
                mainTime2.setText("");
                min1.setText("");
                min2.setText("");


            } else if (minutes.size() == 2) {
                mainTime.setText(String.valueOf(minutes.get(0)));
                mainTime1.setText(String.valueOf(minutes.get(1)));
                mainTime2.setText("");
                min2.setText("");

            } else {
                mainTime.setText(String.valueOf(minutes.get(0)));
                mainTime1.setText(String.valueOf(minutes.get(1)));
                mainTime2.setText(String.valueOf(minutes.get(2)));
            }
        }

        return view;
    }


}
