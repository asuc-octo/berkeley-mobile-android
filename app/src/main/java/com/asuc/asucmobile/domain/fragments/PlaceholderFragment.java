package com.asuc.asucmobile.domain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.asuc.asucmobile.domain.main.CalendarActivity;

/**
 * This fragment is used to create a new intent to open the tabbed Calendar Activity because
 * the Navigation Generator can only open fragments. REEEEEEEE
 */
public class PlaceholderFragment extends Fragment {
    public PlaceholderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getActivity(), CalendarActivity.class);
        startActivity(intent);
    }

}
