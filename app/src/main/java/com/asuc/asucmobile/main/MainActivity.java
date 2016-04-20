package com.asuc.asucmobile.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.flurry.android.FlurryAgent;


public class MainActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");
        setContentView(R.layout.activity_main);

        NavigationGenerator.generateMenu(this);
        NavigationGenerator.openMenu();
        if (getIntent().getExtras() != null) {
            int page = getIntent().getExtras().getInt("page", 0);
            NavigationGenerator.loadSection(page);
        } else {
            NavigationGenerator.loadSection(-1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationGenerator.generateMenu(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        LiveBusActivity.stopBusTracking();
    }

}
