package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Collections;
import java.util.Date;

/**
 * Created by rustie on 9/28/17.
 */

public class OpenCafeActivity extends BaseActivity {

    private static Cafe cafe;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static OpenCafeActivity selfReference;



    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_cafe);
        exitIfNoData();
        setupToolbar(cafe.getName(), true);
        selfReference = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("cafe", cafe.getName());
        mFirebaseAnalytics.logEvent("opened_cafe", bundle);


        // Downloading Dining Hall image
        ImageView headerImage = (ImageView) findViewById(R.id.headerImage);
        Glide.with(this).load(cafe.getImageUrl()).into(headerImage);

        // Load favorites from disk.
        ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(this);
        if (listOfFavorites == null) {
            listOfFavorites = new ListOfFavorites();
            SerializableUtilities.saveObject(this, listOfFavorites);
        }

        sortMenus();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);

            TabLayout tabStrip = (TabLayout) findViewById(R.id.pager_tab_strip);
            tabStrip.setupWithViewPager(viewPager);
            tabStrip.setTabTextColors(getResources().getColor(R.color.off_white), getResources().getColor(R.color.off_white));

            Date currentTime = new Date();

            // display a relevant tab based on time of day
            if (cafe.isLunchDinnerOpen() ||
                    (cafe.getLunchDinnerClosing() != null && currentTime.after(cafe.getLunchDinnerClosing()))) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dining, menu);

        // Make this return true if you would like a menu
        return false;
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
            MenuFragment menuFragment = new MenuFragment();
            Bundle bundle = new Bundle();
            bundle.putString("FoodType", "Cafe");
            menuFragment.setArguments(bundle);

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
                    return "Breakfast";
                case 1:
                    return "Lunch / Dinner";
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

    public static void setCafe(Cafe c) {
        cafe = c;
    }

    private void exitIfNoData() {
        if (cafe == null) {
            finish();
        }
    }

    /**
     * Sort all menus if present
     */
    private void sortMenus() {
        if (cafe.getBreakfastMenu() != null) {
            Collections.sort(cafe.getBreakfastMenu(), CustomComparators.FacilityComparators.getFoodSortByAZ());
            Collections.sort(cafe.getBreakfastMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(selfReference));
        }

        if (cafe.getLunchDinnerMenu() != null) {
            Collections.sort(cafe.getLunchDinnerMenu(), CustomComparators.FacilityComparators.getFoodSortByAZ());
            Collections.sort(cafe.getLunchDinnerMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(selfReference));
        }
    }

}
