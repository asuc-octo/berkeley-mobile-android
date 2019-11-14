package com.asuc.asucmobile.domain.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asuc.asucmobile.GlobalApplication;
import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.LibraryAdapter;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.domain.main.ListOfFavorites;
import com.asuc.asucmobile.domain.main.OpenLibraryActivity;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

public class LibraryFragment extends Fragment {

    public static final String TAG = "LibraryFragment";

    @Inject
    Repository<Library> repository;

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private static LibraryAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GlobalApplication.getRepositoryComponent().inject(this);

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

        final ArrayList<Library> libraries = mAdapter.getLibraries();

        repository.scanAll(libraries, new RepositoryCallback<Library>() {
            @Override
            public void onSuccess() {

                Log.d(TAG, libraries.toString());

                mLibraryList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                // sorted by default
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByFavoriteLibraryThenOpenness(getContext()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

                Log.d(TAG, libraries.toString());

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