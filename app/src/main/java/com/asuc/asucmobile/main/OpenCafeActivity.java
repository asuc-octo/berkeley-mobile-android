package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.CafeController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.Date;

/**
 * Created by rustie on 9/28/17.
 */

public class OpenCafeActivity extends BaseActivity {

    private Cafe cafe;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_cafe);
        exitIfNoData();
        setupToolbar(cafe.getName(), true);

        // Load favorites from disk.
        ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(this);
        if (listOfFavorites == null) {
            listOfFavorites = new ListOfFavorites();
            SerializableUtilities.saveObject(this, listOfFavorites);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(2);

            viewPager.setAdapter(pagerAdapter);
            Date currentTime = new Date();

            // display a relevant tab based on time of day
            if (cafe.isLunchDinnerOpen() ||
                    (cafe.getLunchDinnerClosing() != null && currentTime.after(cafe.getLunchDinnerClosing()))) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }

        }
        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        if (tabStrip != null) {
            tabStrip.setTextColor(getResources().getColor(R.color.off_white));
            tabStrip.setTabIndicatorColor(getResources().getColor(R.color.off_white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dining, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        exitIfNoData();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MenuFragment menuFragment = new MenuFragment(MenuFragment.FoodType.Cafe);
            Bundle bundle = new Bundle(1);

            switch (position) {
                case 0:
                    bundle.putString("whichMenu", "Breakfast");
                    break;
                case 1:
                    bundle.putString("whichMenu", "Lunch/Dinner");
                    break;
                default:
                    return null;
            }

            menuFragment.setArguments(bundle);
            return menuFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "     Breakfast     ";
                case 1:
                    return "    Lunch/Dinner    ";
            }
            return null;
        }
    }



    /**
     * getCafe() returns the DiningHall associated with the latest instance of this Activity.
     * Used to obtain the Cafe from the MenuFragment.
     *
     * @return Cafe of this Activity.
     */
    public Cafe getCafe() {
        return cafe;
    }

    private void exitIfNoData() {
        cafe = ((CafeController) CafeController.getInstance()).getCurrentCafe();
        if (cafe == null) {
            finish();
        }
    }


}
