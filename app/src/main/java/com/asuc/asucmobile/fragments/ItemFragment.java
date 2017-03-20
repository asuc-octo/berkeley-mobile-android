package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import com.asuc.asucmobile.adapters.ItemAdapter;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.controllers.ItemController;
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.main.OpenGymActivity;
import com.asuc.asucmobile.main.OpenLibraryActivity;
import com.asuc.asucmobile.models.Items;
import com.asuc.asucmobile.models.Items.Item;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

import static com.asuc.asucmobile.controllers.Controller.FQDN;

public class ItemFragment extends Fragment {

    private ListView mItemList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private ItemAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("View created");
        View layout = inflater.inflate(R.layout.fragment_item, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle("Search");
        ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(getContext());
        if (listOfFavorites == null) {
            listOfFavorites = new ListOfFavorites();
            SerializableUtilities.saveObject(getContext(), listOfFavorites);
        }
        ImageButton refreshButton = (ImageButton) layout.findViewById(R.id.refresh_button);
        mItemList = (ListView) layout.findViewById(R.id.item_list);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) layout.findViewById(R.id.refresh);
        mAdapter = new ItemAdapter(getContext());
        mItemList.setAdapter(mAdapter);
        mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = mAdapter.getItem(i);
                ItemController.setCurrentItem(mAdapter.getItem(i));
                String URL = FQDN + item.getQuery();
                Intent intent = null;
                switch (item.getCategory()) {
                    case "Dining Hall":
                        ItemController.setItemFromUrl(getContext(),
                                URL, "dining_hall", DiningController.getInstance());
                        intent = new Intent(getContext(), OpenDiningHallActivity.class);
                        break;
                    case "Library":
                        ItemController.setItemFromUrl(getContext(),
                                URL, "library", LibraryController.getInstance());
                        intent = new Intent(getContext(), OpenLibraryActivity.class);
                        break;
                    case "Sports Schedule":
                        //intent = new Intent(getContext(), OpenItemActivity.class);
                        break;
                    case "Group Exercise":
                        //intent = new Intent(getContext(), OpenItemActivity.class);
                        break;
                    case "Gyms":
//                        GymController.setItemFromUrl(getContext(),
//                                URL, "gym", GymController.getInstance());
//                        intent = new Intent(getContext(), OpenGymActivity.class);
                        break;
                    default:
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
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

    //start off lv sorted by favorites
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Collections.sort(mAdapter.getItems());
        mAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            final SearchView searchView = (SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
                // Setting up aesthetics
                EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(getResources().getColor(android.R.color.white));
                searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));

                //Set up by clearing the list.
                final Filter filter = mAdapter.getFilter();
                filter.filter("");

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
            MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    getActivity().onBackPressed();
                    return true;
                }

                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }
            });

            //Expand searchView on entering the view.
            searchMenuItem.expandActionView();
        }
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the item list
     * from the web.
     */
    private void refresh() {
        ItemController.cService controller = Controller.retrofit.create(ItemController.cService.class);
        Call<Items> call = controller.getItems();
        call.enqueue(new retrofit2.Callback<Items>() {
            @Override
            public void onResponse(Call<Items> call, Response<Items> response) {
                if (response.isSuccessful()) {
                    mItemList.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    mAdapter.setList(ItemController.parse(response.body()));
                    System.out.println(response.body());
                } else {
                    System.out.println("abc");
                    onFailure(null, null);
                }
            }

            @Override
            public void onFailure(Call<Items> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
