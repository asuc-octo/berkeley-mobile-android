package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.LibraryAdapter;
import com.asuc.asucmobile.controllers.LibraryController;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends Activity {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private LibraryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_library);

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

                // Flurry log for tapping "Library Hours".
                Map<String, String> libParams = new HashMap<String, String>();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView search = (SearchView) searchViewMenuItem.getActionView();

        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);

        ImageView v = (ImageView) search.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_action_search);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library, menu);

        final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Flurry log for searching in the search bar
                FlurryAgent.logEvent("Tapped on the Search Button (Libraries)");
            }

        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                // Closes the keyboard
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final Filter filter = mAdapter.getFilter();
                filter.filter(s);
                return true;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Flurry log for pressing the back button
        FlurryAgent.logEvent("Tapped on the Back Button (Libraries)");
    }

    /** Updates the visibility of necessary UI elements; refreshes the dining hall list
     *  from the web. */
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
                Toast.makeText(getBaseContext(),
                        "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            }

        });
    }

}