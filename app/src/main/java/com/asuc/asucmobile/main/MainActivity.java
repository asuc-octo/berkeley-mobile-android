package com.asuc.asucmobile.main;

import android.os.Bundle;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;


public class MainActivity extends BaseActivity {

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        // Setup navigation menu.
        NavigationGenerator.openMenu();
        if (getIntent().getExtras() != null) {
            int page = getIntent().getExtras().getInt("page", 0);
            NavigationGenerator.loadSection(page);
        } else {
            NavigationGenerator.loadSection(-1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LiveBusActivity.stopBusTracking();
    }

}
