package com.asuc.asucmobile.domain.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asuc.asucmobile.R;


/**
 * Created by alexthomas on 10/13/17.
 */

public class BusRouteFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bus_list_times, container,
                false);

        return view;


    }
}
