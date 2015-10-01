package com.asuc.asucmobile.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.LibraryAdapter;
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private LibraryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        setContentView(R.layout.activity_library);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mLibraryList = (ListView) findViewById(R.id.library_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        mAdapter = new LibraryAdapter(this);
        mLibraryList.setAdapter(mAdapter);

        mLibraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LibraryController controller = ((LibraryController) LibraryController.getInstance(getBaseContext()));
                controller.setCurrentLibrary(mAdapter.getItem(i));
                Intent intent = new Intent(getBaseContext(), OpenLibraryActivity.class);

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
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Comparator<Library> sortByAZ = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        };
        Comparator<Library> sortByZA = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                return -1 * arg0.getName().compareTo(arg1.getName());
            }
        };
        Comparator<Library> sortByOpenness = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                if (arg0.isOpen() && arg1.isOpen()) return 0;
                if (arg0.isOpen()) return -1;
                if (arg1.isOpen()) return 1;
                return 0;
            }
        };

        switch (menuItem.getItemId()){
            case R.id.sortAZ:
                Collections.sort(mAdapter.getLibraries(), sortByAZ);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.sortZA:
                Collections.sort(mAdapter.getLibraries(), sortByZA);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.sortOpen:
                Collections.sort(mAdapter.getLibraries(), sortByOpenness);
                mAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Flurry logging for pressing the Back Button
        FlurryAgent.logEvent("Tapped on the Back Button (Libraries)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        if (searchMenuItem != null) {
            final SearchView searchView = (SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
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

        return true;
    }

    /**
     * refresh() updates the visibility of necessary UI elements and refreshes the library list
     * from the web.
     */
    private void refresh() {
        mLibraryList.setVisibility(View.GONE);
        mRefreshWrapper.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        LibraryController.getInstance(this).refreshInBackground(new Callback() {
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
                Toast.makeText(getBaseContext(), "Unable to retrieve data, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
