package com.asuc.asucmobile.domain.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asuc.asucmobile.R;


/**
 * Created by alexthomas on 10/14/17.
 */

public class BusFailedFragment extends Fragment {
    TextView markerTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bus_failed, container,
                false);

        markerTitle = (TextView) view.findViewById(R.id.busStopName);
        markerTitle.setText(getArguments().getString("title"));


        return view;

    }
}

