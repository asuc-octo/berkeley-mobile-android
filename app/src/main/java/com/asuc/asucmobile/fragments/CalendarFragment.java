package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.CalendarAdapter;
import com.asuc.asucmobile.models.CalendarItem;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

public class CalendarFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;

    ArrayList<CalendarItem> calendarItems;
    private static CalendarAdapter adapter;
    private ListView listView;

    private static final String ARG_SECTION_NUMBER = "calendar_number";

    public CalendarFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarFragment newInstance(int sectionNumber) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    private static final String[] dates = new String[] {"February 1st", "February 13th", "April 5th", "May 3rd",
            "May 6th-10th", "May 10th"};

    private static final String[] info = new String[] {"Early Drop Deadline (EDD)",
            "Deadline to drop non EDD courses, Deadline to add all classes",
            "Deadline to change course grading option",
            "Deadline to submit late change of schedule petition",
            "RRR period",
            "Deadline to withdraw"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarItems = new ArrayList<>();

        int calendar_number = getArguments().getInt("calendar_number");

        Log.d("SECTION NUMBER", String.valueOf(calendar_number));

        switch (calendar_number) {
            case 0: // all
//                toolbar.setTitle("All");
                calendarItems.add(new CalendarItem("DATE", "TITLE", true));
                for (int i = 0; i < dates.length; i++) {
                    calendarItems.add(new CalendarItem("all", "all"));
                }
                break;
            case 1: // ls
//                toolbar.setTitle("L&S Academic Calendar");
                calendarItems.add(new CalendarItem("DATE", "TITLE", true));
                for (int i = 0; i < dates.length; i++) {
                    calendarItems.add(new CalendarItem(dates[i], info[i]));
                }
                break;
            case 2: // w/e
//                toolbar.setTitle("we");
                calendarItems.add(new CalendarItem("DATE", "TITLE", true));
                for (int i = 0; i < dates.length; i++) {
                    calendarItems.add(new CalendarItem("we", "we"));
                }
                break;

        }

        adapter = new CalendarAdapter(calendarItems, getContext());
        listView = (ListView) layout.findViewById(R.id.list);
        listView.setAdapter(adapter);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }
}
