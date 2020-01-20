package com.asuc.asucmobile.domain.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;


import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.PlaceArrayAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceBuffer;
import com.google.android.libraries.places.compat.Places;
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;
/**
 * Created by alexthomas on 5/28/17.
 */

public class OriginFragment extends PlaceAutocompleteFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private static View layout;

    private TextView mNameView;

    private static GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_BERKELEY = new LatLngBounds(
            new LatLng(37.854866, -122.269154), new LatLng(37.883038, -122.245781));
    private LatLng origin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
        }
        try {
            layout = inflater.inflate(R.layout.origin_fragment, container, false);
        } catch (InflateException e) {
            Log.e("hi", e.toString());
            // Don't worry about it!
        }


        mAutocompleteTextView = (AutoCompleteTextView) layout.findViewById(R.id.origin);
        mAutocompleteTextView.setThreshold(3);

        final ImageView clear = (ImageView) layout.findViewById(R.id.clear_button);

        final ImageView searchButton = (ImageView) layout.findViewById(R.id.search_button);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                try {
                    origin = MapsFragment.getInstance().getCurrLocation();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(origin.latitude, origin.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0);
                    mAutocompleteTextView.setText(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) layout.getContext(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mAutocompleteTextView.setTextColor(getResources().getColor(R.color.ASUC_blue));
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setVisibility(View.GONE);
                clear.setVisibility(View.VISIBLE);
                mAutocompleteTextView.setHint("Current Location");
                try {
                    clear.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    origin = MapsFragment.getInstance().getCurrLocation();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(origin.latitude, origin.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0);
                    mAutocompleteTextView.setText(address);
                } catch (Exception e) {

                }


            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteTextView.setText("");
                clear.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                origin = null;

            }
        });

        AutocompleteFilter filter =
                new AutocompleteFilter.Builder().setCountry("US").build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), R.layout.maps_autocomplete_list_item, R.id.text1,
                BOUNDS_BERKELEY, filter);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
       //mAutocompleteTextView.setText("Current Location");
        //origin = MapsFragment.getInstance().getCurrLocation();

        return layout;
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            origin = place.getLatLng();


        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);

            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public LatLng getOrigin() {

        return origin;
    }
}
