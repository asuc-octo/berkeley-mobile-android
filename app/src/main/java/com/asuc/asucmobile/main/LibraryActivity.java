package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.utilities.Callback;

import java.util.ArrayList;

public class LibraryActivity extends Activity {

    private ListView mLibraryList;
    private ProgressBar mProgressBar;
    private LinearLayout mRefreshWrapper;

    private LibraryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText librarySearch = (EditText) findViewById(R.id.search_libraries);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);

        mLibraryList = (ListView) findViewById(R.id.library_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRefreshWrapper = (LinearLayout) findViewById(R.id.refresh);

        mAdapter = new LibraryAdapter(this);
        mLibraryList.setAdapter(mAdapter);

        final Filter filter = mAdapter.getFilter();
        librarySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                filter.filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mLibraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenLibraryActivity.library = mAdapter.getItem(i);
                Intent intent = new Intent(getBaseContext(), OpenLibraryActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // TODO: Go back to previous activity.
            return true;
        }
        return super.onOptionsItemSelected(item);
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
