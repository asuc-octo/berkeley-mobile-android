package com.asuc.asucmobile.domain.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


public class MainActivity extends BaseActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String TAG = "MainActivity";

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        // Setup navigation menu.
        NavigationGenerator.getInstance().openMenu(this);
        if (getIntent().getExtras() != null) {
            int page = getIntent().getExtras().getInt("page", 0);
            NavigationGenerator.getInstance().loadSection(this, page);
        } else {
            NavigationGenerator.getInstance().loadSection(this, -1);
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchConfig();


    }

    private void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig.activateFetched();
                    } else {
                        Toast.makeText(MainActivity.this, "Fetch Failed",
                        Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LiveBusActivity.stopBusTracking();
    }

}
