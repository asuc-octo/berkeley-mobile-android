package com.asuc.asucmobile.domain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.asuc.asucmobile.domain.main.CalendarActivity;

import lombok.NoArgsConstructor;


/**
 * This fragment is used to create a new intent to open the tabbed Calendar Activity because
 * the Navigation Generator can only open fragments. REEEEEEEE
 */
@NoArgsConstructor
public class PlaceholderFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getActivity(), CalendarActivity.class);
        startActivity(intent);
    }

}
