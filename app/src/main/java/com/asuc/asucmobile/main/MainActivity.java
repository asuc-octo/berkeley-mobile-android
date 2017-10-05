package com.asuc.asucmobile.main;

import android.app.Activity;
import android.os.Bundle;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;


public class MainActivity extends BaseActivity {
    private final Activity activity = this;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        // Setup navigation menu.
        //NavigationGenerator.getInstance().openMenu(this);
        if (getIntent().getExtras() != null) {
            int page = getIntent().getExtras().getInt("page", 0);
            NavigationGenerator.getInstance().loadSection(this, page);
        } else {
            NavigationGenerator.getInstance().loadSection(this, -1);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        LiveBusActivity.stopBusTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
