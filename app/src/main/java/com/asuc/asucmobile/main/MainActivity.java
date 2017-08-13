package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.CardAdapter;
import com.asuc.asucmobile.controllers.DiningCardController;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.controllers.GymCardController;
import com.asuc.asucmobile.fragments.DiningHallFragment;
import com.asuc.asucmobile.fragments.GymFragment;
import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.models.Gyms.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {
    private final Activity activity = this;

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
