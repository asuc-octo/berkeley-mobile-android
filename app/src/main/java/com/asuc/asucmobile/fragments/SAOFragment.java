package com.asuc.asucmobile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;

public class SAOFragment extends Fragment {

    private static final String SAO_DESCRIPTION_SHORT = "The Student Advocate’s Office (SAO) is...";
    private static final String SAO_DESCRIPTION_LONG = "The Student Advocate’s Office (SAO) is a " +
            "nonpartisan executive office of the ASUC that provides free and confidential advice " +
            "to all students. SAO provides assistance with conduct, financial aid, academic, and " +
            "grievance related issues.";
    private static final String BUTTON_COLOR = "#005581";

    private boolean textIsShortened = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_sao, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle("Student Advocate Office");

        final TextView description = (TextView) layout.findViewById(R.id.sao_description);
        description.setText(createDescriptionText(SAO_DESCRIPTION_SHORT, " more"));
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textIsShortened) {
                    description.setText(createDescriptionText(SAO_DESCRIPTION_LONG, " less"));
                    textIsShortened = false;
                } else {
                    description.setText(createDescriptionText(SAO_DESCRIPTION_SHORT, " more"));
                    textIsShortened = true;
                }
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }

    private SpannableString createDescriptionText(String text, String button) {
        SpannableString description = new SpannableString(text + button);
        ForegroundColorSpan buttonSpan = new ForegroundColorSpan(Color.parseColor(BUTTON_COLOR));
        description.setSpan(buttonSpan, text.length(), description.length(), 0);
        return description;
    }

}
