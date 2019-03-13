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

    private static final String[] dates_academic = new String[] {"January 15, 2019", "January 21, 2019",
            "January 22, 2019", "February 18, 2019", "March 25–March 29, 2019",
            "March 29, 2019", "April 13, 2019", "May 3, 2019", "May 6–May 10, 2019",
            "May 10, 2019", "May 13–May 17, 2019", "May 17, 2019", "May 18, 2019",
            "May 27, 2019"
    };

    private static final String[] dates_finalExam = new String[] {"May 13, 2019, 8–11 am", "May 13, 2019, 11:30-2:30 pm", "May 13, 2019, 3-6 pm","May 13, 2019, 7-10 pm",
            "May 14, 2019 8-11 am", "May 14, 2019, 11:30-2:30 pm",
            "May 14, 2019, 3-6 pm", "May 14, 2019, 7-10 pm",
            "May 15, 2019 8-11 am", "May 15, 2019, 11:30-2:30 pm",
            "May 15, 3-6 pm", "May 15, 2019, 7-10 pm",
            "May 16, 2019 8-11 am", "May 16, 2019, 11:30-2:30 pm",
            "May 16, 3-6 pm", "May 16, 2019, 7-10 pm",
            "May 17, 2019 8-11 am", "May 17, 2019, 11:30-2:30 pm",
            "May 17, 2019, 3-6 pm", "May 17, 2019, 7-10 pm"
    };

    private static final String[] info_ls = new String[] {"Early Drop Deadline (EDD)",
            "Deadline to drop non EDD courses, Deadline to add all classes",
            "Deadline to change course grading option",
            "Deadline to submit late change of schedule petition",
            "RRR period",
            "Deadline to withdraw"};


    private static final String[] info_academic = new String[] {"Spring Semester Begins","Academic and Administrative Holiday",
            "Instruction Begins","Academic and Administrative Holiday","Spring Recess","Academic and Administrative Holiday",
            "Cal Day","Formal Classes End","Reading/Review/Recitation Week","Last Day of Instruction","Final Examinations",
            "Spring Semester Ends", "Commencement", "Academic and Administrative Holiday"};

    private static final String[] info_finalExam = new String[] {"MWF & MTWTF, 8 am", "TuTh, 2 pm", "Econ 1 & 100B", "MWF & MTWTF, 9 & 9:30 am", "MWF & MTWTF, 1 pm", "MWF & MTWTF, 2 pm", "MWF & MTWTF, 10 am", "MWF & MTWTF, 11 am", "Chem 1A, 1B, 3A, 3B, 4A, & 4B", "TuTh, 9 & 9:30 am", "MWF & MTWTF, 12 & 12:30 pm", "MWF & MTWTF, 3 & 3:30 pm", "TuTh, 11 am", "Online courses* & Elementary Foreign Languages**",
            "TuTh, 12, 12:30, & 1 pm", "TuTh, 8 am and all Saturday & Sunday", "MWF & MTWTF, 4 & 4:30 pm", "TuTh, at or after 5 pm", "TuTh, 10 am; English 1A, 1B, R1A, R1B; MWF, at or after 5 pm", "TuTh, 3, 3:30, & 4 pm"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarItems = new ArrayList<>();

        int calendar_number = getArguments().getInt("calendar_number");

        switch (calendar_number) {
            case 0:
                calendarItems.add(new CalendarItem("DATE", "EVENT", true));
                for (int i = 0; i < dates_academic.length; i++) {
                    calendarItems.add(new CalendarItem(dates_academic[i], info_academic[i], false));
                }
                break;
            case 1:
                calendarItems.add(new CalendarItem("EXAM TIME", "FOR CLASS START TIMES", true));
                for (int i = 0; i < dates_finalExam.length; i++) {
                    calendarItems.add(new CalendarItem(dates_finalExam[i], info_finalExam[i], false));
                }
                break;
            case 2:
                calendarItems.add(new CalendarItem("DATE", "EVENT", true));
                for (int i = 0; i < dates_ls.length; i++) {
                    calendarItems.add(new CalendarItem(dates_ls[i], info_ls[i], false));
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
