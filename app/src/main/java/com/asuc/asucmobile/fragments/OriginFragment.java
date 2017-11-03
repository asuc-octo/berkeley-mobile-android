package com.asuc.asucmobile.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MarkerAdapter;
import com.asuc.asucmobile.models.customMarker;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexthomas on 5/28/17.
 */

public class OriginFragment extends Fragment {

    private static final ArrayList<customMarker> BERKELEY_PLACES =
            new ArrayList();
    private MarkerAdapter adapter;
    private AutoCompleteTextView autoCompleteTextView;
    private static final HashMap<String, Marker> all_markers = new HashMap<>();
    private Marker selectedMarker = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        adapter = new MarkerAdapter(getActivity(),
                android.R.layout.simple_list_item_activated_1, BERKELEY_PLACES);

        View view = inflater.inflate(R.layout.origin_fragment, container, false);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.destination);
        autoCompleteTextView.setAdapter(adapter);
        final ImageView clear = (ImageView) view.findViewById(R.id.clear_button);

        final ImageView searchButton = (ImageView) view.findViewById(R.id.search_button);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autoCompleteTextView.setTextColor(getResources().getColor(R.color.ASUC_blue));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    clear.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.GONE);
                } else {
                    clear.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);

                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
                clear.setVisibility(View.GONE);
                selectedMarker = null;
                searchButton.setVisibility(View.VISIBLE);

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customMarker marker = (customMarker) parent.getItemAtPosition(position);
                selectedMarker = marker.marker;
                if (marker != null) {
                    //  ((MapsFragment) getActivity()).markerZoom(marker.marker);
                }
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MapsFragment.getInstance().getNearbyButton().setVisibility(View.INVISIBLE);
                } else {
                    MapsFragment.getInstance().getNearbyButton().setVisibility(View.VISIBLE);
                    hideKeyboard(getView());

                }
            }
        });

        return view;

    }

    public void loadData() {

        if (BERKELEY_PLACES.size() == 0) {
            final ArrayList<Marker> AC_names = MapsFragment.getInstance().getAC_Names();
            for (int i = 0; i < AC_names.size(); i += 1) {
                BERKELEY_PLACES.add(new customMarker(AC_names.get(i).getTitle(), AC_names.get(i)));
            }
        }else{
            //Do nothing!
        }


        adapter.notifyDataSetChanged();

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Marker getOrigin() {

        return selectedMarker;
    }
}
