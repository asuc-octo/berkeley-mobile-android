package com.asuc.asucmobile.main;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.asuc.asucmobile.R;

public class DiningHallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            TextView titleText = (TextView) findViewById(titleId);
            titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));
        }
        setContentView(R.layout.activity_dining_hall);
    }

}
