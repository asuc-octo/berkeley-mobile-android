package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class OpenDiningHallActivity extends BaseActivity {

    private static final String[] LATE_NIGHT_LOCATIONS = {"Crossroads","Foothill"};

    private DiningHall diningHall;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_dining_hall);
        exitIfNoData();
        setupToolbar(diningHall.getName(), true);

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
            if (Arrays.asList(LATE_NIGHT_LOCATIONS).contains(diningHall.getName())) {
                viewPager.setOffscreenPageLimit(4);
            } else {
                viewPager.setOffscreenPageLimit(3);
            }
            viewPager.setAdapter(pagerAdapter);
            Date currentTime = new Date();
            if (diningHall.isLateNightOpen() ||
                    (diningHall.getDinnerClosing() != null && currentTime.after(diningHall.getDinnerClosing()))) {
                viewPager.setCurrentItem(3);
            } else if (diningHall.isDinnerOpen() ||
                    (diningHall.getLunchClosing() != null && currentTime.after(diningHall.getLunchClosing())) ||
                    (diningHall.getDinnerClosing() != null && currentTime.after(diningHall.getDinnerClosing()))) {
                viewPager.setCurrentItem(2);
            } else if (diningHall.isLunchOpen() ||
                    (diningHall.getBreakfastClosing() != null && currentTime.after(diningHall.getBreakfastClosing()))) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
        }
        TabLayout tabStrip = (TabLayout) findViewById(R.id.pager_tab_strip);
        tabStrip.setupWithViewPager(viewPager);
        if (tabStrip != null) {
            tabStrip.setTabTextColors(getResources().getColor(R.color.off_white), getResources().getColor(R.color.off_white));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.sortAZ) {
            DiningHall diningHall = MenuFragment.getDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByAZ());

            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByAZ());

            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByAZ());

            ArrayList<FoodItem> arrayListLateNight = diningHall.getLateNightMenu();
            Collections.sort(arrayListLateNight, CustomComparators.FacilityComparators.getFoodSortByAZ());

            MenuFragment.refreshLists();
            return true;
        }
        if (id == R.id.sortFavorites) {
            DiningHall diningHall = MenuFragment.getDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByFavorite(this));

            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByFavorite(this));

            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByFavorite(this));

            ArrayList<FoodItem> arrayListLateNight = diningHall.getLateNightMenu();
            Collections.sort(arrayListLateNight, CustomComparators.FacilityComparators.getFoodSortByFavorite(this));

            MenuFragment.refreshLists();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            MenuFragment menuFragment = new MenuFragment();
            Bundle bundle = new Bundle(1);

            // If late night exists in this dining hall, add it; otherwise, leave it out.
            if (Arrays.asList(LATE_NIGHT_LOCATIONS).contains(diningHall.getName())) {
                switch (position) {
                    case 0:
                        bundle.putString("whichMenu", "Breakfast");
                        break;
                    case 1:
                        bundle.putString("whichMenu", "Lunch");
                        break;
                    case 2:
                        bundle.putString("whichMenu", "Limited");
                        break;
                    case 3:
                        bundle.putString("whichMenu", "Dinner");
                        break;
                    case 4:
                        bundle.putString("whichMenu", "Limited");
                        break;
                    default:
                        return null;
                }
            } else {
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
            }
            menuFragment.setArguments(bundle);
            return menuFragment;
        }

        @Override
        public int getCount() {
            if (Arrays.asList(LATE_NIGHT_LOCATIONS).contains(diningHall.getName())) {
                return 5;
            } else {
                return 3;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Only set up a Late Night option if it exists.
            if (Arrays.asList(LATE_NIGHT_LOCATIONS).contains(diningHall.getName())) {
                switch (position) {
                    case 0:
                        return "Breakfast";
                    case 1:
                        return "Lunch";
                    case 2:
                        return "Limited";
                    case 3:
                        return "Dinner";
                    case 4:
                        return "Limited";
                }
            } else {
                switch (position) {
                    case 0:
                        return "Breakfast";
                    case 1:
                        return "Lunch";
                    case 2:
                        return "Dinner";
                }
            }
            return null;
        }
    }

    /**
     * getDiningHall() returns the DiningHall associated with the latest instance of this Activity.
     * Used to obtain the DiningHall from the MenuFragment.
     *
     * @return DiningHall of this Activity.
     */
    public DiningHall getDiningHall() {
        return diningHall;
    }

    private void exitIfNoData() {
        diningHall = ((DiningController) DiningController.getInstance()).getCurrentDiningHall();
        if (diningHall == null) {
            finish();
        }
    }

}