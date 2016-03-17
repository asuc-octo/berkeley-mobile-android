package com.asuc.asucmobile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flurry.android.FlurryAgent;


public class MainActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");
        Intent start = new Intent(this, StartStopSelectActivity.class);
        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(start);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
