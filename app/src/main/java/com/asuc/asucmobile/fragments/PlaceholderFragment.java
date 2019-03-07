package com.asuc.asucmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.main.CalendarActivity;

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
