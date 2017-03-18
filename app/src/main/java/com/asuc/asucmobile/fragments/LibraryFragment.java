package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;
import java.util.Collections;

public class LibraryFragment extends Fragment {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;
    private SearchView mSearchView;

    private LibraryAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_library, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
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
        mSearchView = (SearchView) layout.findViewById(R.id.local_search);
        mAdapter = new LibraryAdapter(getContext());
        mLibraryList.setAdapter(mAdapter);
        mLibraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LibraryController controller = (LibraryController) LibraryController.getInstance();
                controller.setCurrentLibrary(mAdapter.getItem(i));
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
        if (mSearchView != null) {
            mSearchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchView.onActionViewExpanded();
                }
            });
            // Setting up aesthetics
            EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.grizzly_gray));
            searchEditText.setHintTextColor(getResources().getColor(R.color.grizzly_gray));

            //Set up by clearing the list.
            final Filter filter = mAdapter.getFilter();
            filter.filter("");

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // Close the keyboard
                    mSearchView.clearFocus();
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
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationGenerator.closeMenu(getActivity());
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
            searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem m) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    return fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new ItemFragment())
                            .addToBackStack("tag")
                            .commit() > 0;
                }
            });

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
