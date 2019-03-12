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

    private static final String[] info_calendar = new String[] {"L&S Deadlines", "Academic Calendar"};

    private static final String[] dates_ls = new String[] {"February 1, 2019", "February 13, 2019", "April 5, 2019", "May 3, 2019",
            "May 6-10, 2019", "May 10, 2019"};

    private static final String[] info_ls = new String[] {"Early Drop Deadline (EDD)",
            "Deadline to drop non EDD courses, Deadline to add all classes",
            "Deadline to change course grading option",
            "Deadline to submit late change of schedule petition",
            "RRR period",
            "Deadline to withdraw"};

    private static final String[] dates_academic = new String[] {"Tuesday, January 15, 2019", "Monday, January 21, 2019",
            "Tuesday, January 22, 2019", "Monday, February 18, 2019", "Monday, March 25–Friday, March 29, 2019",
            "Friday, March 29, 2019", "Saturday, April 13, 2019", "Friday, May 3, 2019", "Monday, May 6–Friday, May 10, 2019",
            "Friday, May 10, 2019", "Monday, May 13–Friday, May 17, 2019", "Friday, May 17, 2019", "Saturday, May 18, 2019",
            "Monday, May 27, 2019"
            };

    private static final String[] info_academic = new String[] {"Spring Semester Begins","Academic and Administrative Holiday",
            "Instruction Begins","Academic and Administrative Holiday","Spring Recess","Academic and Administrative Holiday",
            "Cal Day","Formal Classes End","Reading/Review/Recitation Week","Last Day of Instruction","Final Examinations",
            "Spring Semester Ends", "Commencement", "Academic and Administrative Holiday"};

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
                for (int i = 0; i < info_calendar.length; i++) {
                    calendarItems.add(new CalendarItem("Spring 2019", info_calendar[i]));
                }
                break;
            case 1: // ls
//                toolbar.setTitle("L&S Academic Calendar");
                calendarItems.add(new CalendarItem("DATE", "TITLE", true));
                for (int i = 0; i < dates_ls.length; i++) {
                    calendarItems.add(new CalendarItem(dates_ls[i], info_ls[i]));
                }
                break;
            case 2: // w/e
//                toolbar.setTitle("we");
                calendarItems.add(new CalendarItem("DATE", "TITLE", true));
                for (int i = 0; i < dates_academic.length; i++) {
                    calendarItems.add(new CalendarItem(dates_academic[i], info_academic[i]));
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
