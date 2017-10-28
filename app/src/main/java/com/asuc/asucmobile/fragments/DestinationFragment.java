package com.asuc.asucmobile.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MarkerAdapter;
import com.asuc.asucmobile.models.customMarker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;


import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by alexthomas on 5/26/17.
 */

public class DestinationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView autoCompleteTextView;
    private MarkerAdapter adapter;
    private static final ArrayList<customMarker> BERKELEY_PLACES =
            new ArrayList();
    private static final HashMap<String, Marker> all_markers = new HashMap<>();
    private Marker selectedMarker = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.destination_fragment, container,
                false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        adapter = new MarkerAdapter(getActivity(),
                android.R.layout.simple_list_item_activated_1, BERKELEY_PLACES);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.destination);

        final ImageView searchButton = (ImageView) view.findViewById(R.id.search_button);

        autoCompleteTextView.setAdapter(adapter);

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

        final ImageView clear = (ImageView) view.findViewById(R.id.clear_button);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
                selectedMarker = null;
                clear.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);

            }
        });

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

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customMarker marker = (customMarker) parent.getItemAtPosition(position);
                selectedMarker = marker.marker;
                if (marker != null) {
                    MapsFragment.getInstance().markerZoom(marker.marker);
                }
            }
        });


        return view;


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(), "Could not connect to Google API Client: Error "
                + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public Marker getDestination() {
        return selectedMarker;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        loadData();
    }
}
