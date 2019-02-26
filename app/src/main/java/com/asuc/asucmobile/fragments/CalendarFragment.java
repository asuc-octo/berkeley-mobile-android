package com.asuc.asucmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

public class CalendarFragment extends Fragment {
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String[] dates = new String[] {"February 1st", "February 13th", "April 5th", "May 3rd",
            "May 3rd", "May 6th-10th", "May 10th"};

    private static final String[] info = new String[] {"Deadline to drop Early Drop Deadline (EDD)",
            "Deadline to drop non EDD courses\nDeadline to add all classes",
            "Deadline to change course grading option",
            "Deadline to submit late change of schedule petition",
            "RRR period",
            "Deadline to withdraw"};

    private ListView datesView;
    private ListView infoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("L&S Academic Calendar");

        datesView = layout.findViewById(R.id.dates);
        infoView = layout.findViewById(R.id.info);

        datesView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, Arrays.asList((dates))));

        infoView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, Arrays.asList((info))));

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }
}
