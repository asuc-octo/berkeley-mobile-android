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
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.main.OpenLibraryActivity;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LibraryFragment extends Fragment {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private LibraryAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FlurryAgent.onStartSession(getContext(), "4VPTT49FCCKH7Z2NVQ26");
        View layout = inflater.inflate(R.layout.fragment_library, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Libraries");

        ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(getContext());
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
                LibraryController controller = (LibraryController) LibraryController.getInstance();
                controller.setCurrentLibrary(mAdapter.getItem(i));
                Intent intent = new Intent(getContext(), OpenLibraryActivity.class);

                //Flurry log for tapping Library hours.
                Map<String, String> libParams = new HashMap<>();
                libParams.put("Hall", mAdapter.getItem(i).getName());
                FlurryAgent.logEvent("Taps Library Hours", libParams);

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
        NavigationGenerator.closeMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(getContext());
    }

    //start off lv sorted by favorites
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.sortAZ:
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByAZ());
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.sortOpen:
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByOpenness());
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.sortFavorites:
                Collections.sort(mAdapter.getLibraries(), CustomComparators.FacilityComparators.getSortByFavoriteLibrary(getContext()));
                mAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.library, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        if (searchMenuItem != null) {
            final SearchView searchView = (SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
                // Setting up aesthetics
                EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(getResources().getColor(android.R.color.white));
                searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));

                searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Flurry log for searching for something!
                        FlurryAgent.logEvent("Tapped on the Search Button (Libraries)");
                    }
                });
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // Close the keyboard
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        final Filter filter = mAdapter.getFilter();
                        filter.filter(s);
                        return true;
                    }
                });
            }
        }
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the library list
     * from the web.
     */
    private void refresh() {
        mLibraryList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        LibraryController.getInstance().refreshInBackground(getActivity(), new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                mLibraryList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

                mAdapter.setList((ArrayList<Library>) data);
            }

            @Override
            public void onRetrievalFailed() {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
