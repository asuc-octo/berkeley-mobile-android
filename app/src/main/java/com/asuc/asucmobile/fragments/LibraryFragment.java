package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.LibraryAdapter;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.main.OpenLibraryActivity;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.models.responses.LibrariesResponse;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private static LibraryAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;



    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("opened_library_screen", bundle);

        View layout = inflater.inflate(R.layout.fragment_library, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Libraries");
        ListOfFavorites listOfFavorites =
                (ListOfFavorites) SerializableUtilities.loadSerializedObject(getContext());
        if (listOfFavorites == null) {
            listOfFavorites = new ListOfFavorites();
            SerializableUtilities.saveObject(getContext(), listOfFavorites);
        }
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mLibraryList = (ListView) layout.findViewById(R.id.library_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        mAdapter = new LibraryAdapter(getContext());
        mLibraryList.setAdapter(mAdapter);
        mLibraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenLibraryActivity.setLibrary(mAdapter.getItem(i));
                Intent intent = new Intent(getContext(), OpenLibraryActivity.class);
                startActivity(intent);
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        refresh();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
    }


    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the library list
     * from the web.
     */
    private void refresh() {
        mLibraryList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        BMRetrofitController.bmapi.callLibrariesList().enqueue(new retrofit2.Callback<LibrariesResponse>() {
            @Override
            public void onResponse(Call<LibrariesResponse> call, Response<LibrariesResponse> response) {
                mLibraryList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.setList(response.body().getLibraries());

                // sorted by default
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByAZ());
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByFavoriteLibrary(getContext()));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<LibrariesResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void refreshLists() {
        if (LibraryFragment.mAdapter == null)
            return;
        mAdapter.notifyDataSetChanged();

    }
}
