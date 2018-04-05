package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.provider.Settings;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.BMAPI;
import com.asuc.asucmobile.controllers.BMRetrofitController;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getName();
    private static final int DURATION = 500; // in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes notification bar
        setContentView(R.layout.activity_splash);

        // disable Firebase crash reporting for developers
        int adb = Settings.Secure.getInt(this.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);
        if (adb != 1) {
            Fabric.with(this, new Crashlytics());
        }

        // launch network stuff
        BMRetrofitController.create(this, BMAPI.class);

        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    private class IntentLauncher extends Thread {

        /**
         * Sleep for some time and than start new activity.
         */
        @Override
        public void run() {
            try {
                // Sleeping (in milliseconds)
                Thread.sleep(DURATION);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            // Start main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}