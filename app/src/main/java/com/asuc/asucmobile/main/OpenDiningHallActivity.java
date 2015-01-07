package com.asuc.asucmobile.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.DiningHall;
import com.flurry.android.FlurryAgent;

public class OpenDiningHallActivity extends Activity implements ActionBar.TabListener {

    private DiningHall diningHall;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        diningHall = ((DiningController) DiningController.getInstance(this)).getCurrentDiningHall();

        if (diningHall == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_open_dining_hall);

        // Sets up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(diningHall.getName());

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");

        TextView titleText = (TextView) findViewById(titleId);
        titleText.setTypeface(Typeface.createFromAsset(getAssets(), "young.ttf"));

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Creates the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Sets up the ViewPager with the sections adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);

        // Selects the correct tab when the user swipes between sections. Also uses
        // ActionBar.Tab#select() if there is a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

        });

        // Adds a tab to the action bar for each section in the app.
        // Creates a tab with text that corresponds to the page title defined by the adapter.
        // Specifies this Activity object, implementing the TabListener interface, as the
        // call back (listener) for when this tab is selected.
        for (int i = 0; i < pagerAdapter.getCount(); i++) {

            actionBar.addTab(actionBar.newTab()
                    .setText(pagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        diningHall = ((DiningController) DiningController.getInstance(this)).getCurrentDiningHall();

        if (diningHall == null) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


    /** Switches to the corresponding page in the ViewPager when the given tab is selected. */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
     *  sections, tabs, or pages. */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MenuFragment menuFragment = new MenuFragment();
            Bundle bundle = new Bundle(1);

            switch (position) {
                case 0:
                    bundle.putString("whichMenu", "Breakfast");
                    break;
                case 1:
                    bundle.putString("whichMenu", "Lunch");
                    break;
                case 2:
                    bundle.putString("whichMenu", "Dinner");
                    break;
                default:
                    return null;
            }

            menuFragment.setArguments(bundle);

            return menuFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Breakfast";
                case 1:
                    return "Lunch";
                case 2:
                    return "Dinner";
            }
            return null;
        }
    }

    /** Returns the DiningHall associated with the latest instance of this Activity.
     *  Used to obtain the DiningHall from the MenuFragment. */
    public DiningHall getDiningHall() {
        return diningHall;
    }

}