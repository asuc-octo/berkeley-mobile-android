package com.asuc.asucmobile.domain.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.CalendarAdapter;
import com.asuc.asucmobile.models.CalendarItem;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.values.CalendarItems;

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarItems = new ArrayList<>();

        int calendar_number = getArguments().getInt("calendar_number");

        switch (calendar_number) {
            case 0:
                calendarItems.add(new CalendarItem("DATE", "EVENT", true));
                for (int i = 0; i < CalendarItems.DATES_ACADEMIC.length; i++) {
                    calendarItems.add(new CalendarItem(CalendarItems.DATES_ACADEMIC[i], CalendarItems.INFO_ACADEMIC[i], false));
                }
                break;
            case 1:
                calendarItems.add(new CalendarItem("EXAM TIME", "FOR CLASS START TIMES", true));
                for (int i = 0; i < CalendarItems.DATES_FINAL.length; i++) {
                    calendarItems.add(new CalendarItem(CalendarItems.DATES_FINAL[i], CalendarItems.INFO_FINAL[i], false));
                }
                break;
            case 2:
                calendarItems.add(new CalendarItem("DATE", "EVENT", true));
                for (int i = 0; i < CalendarItems.DATES_LS.length; i++) {
                    calendarItems.add(new CalendarItem(CalendarItems.DATES_LS[i], CalendarItems.INFO_LS[i], false));
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
