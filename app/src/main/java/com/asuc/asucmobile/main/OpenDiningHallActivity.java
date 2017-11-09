package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.SerializableUtilities;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

public class OpenDiningHallActivity extends BaseActivity {

    private static final String[] LIMITED_LOCATIONS = {"Crossroads","Foothill"};
    private DiningHall diningHall;
    public static OpenDiningHallActivity self_reference;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_open_dining_hall);
        exitIfNoData();
        setupToolbar(diningHall.getName(), true);
        self_reference = OpenDiningHallActivity.this;

        // Downloading Dining Hall image
        ImageView headerImage = (ImageView) findViewById(R.id.headerImage);
        new DownloadImageThread(headerImage, diningHall.getImageUrl()).start();


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
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);

            TabLayout tabStrip = (TabLayout) findViewById(R.id.pager_tab_strip);
            tabStrip.setupWithViewPager(viewPager);
            tabStrip.setTabTextColors(getResources().getColor(R.color.off_white), getResources().getColor(R.color.off_white));

            if (Arrays.asList(LIMITED_LOCATIONS).contains(diningHall.getName())) {
                viewPager.setOffscreenPageLimit(5);
                tabStrip.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                viewPager.setOffscreenPageLimit(3);
            }

            // Finds the current tab
            Date currentTime = new Date();
            if (diningHall.isLimitedDinnerOpen() ||
                    (diningHall.getDinnerClosing() != null && currentTime.after(diningHall.getDinnerClosing()))) {
                viewPager.setCurrentItem(3);
            } else if (diningHall.isDinnerOpen() ||
                    (diningHall.getLunchClosing() != null && currentTime.after(diningHall.getLunchClosing())) ||
                    (diningHall.getDinnerClosing() != null && currentTime.after(diningHall.getDinnerClosing()))) {
                viewPager.setCurrentItem(2);
            } else if (diningHall.isLimitedLunchOpen() ||
                    (diningHall.getLunchClosing() != null && currentTime.after(diningHall.getLunchClosing()))) {
                viewPager.setCurrentItem(1);
            } else if (diningHall.isLunchOpen() ||
                    (diningHall.getBreakfastClosing() != null && currentTime.after(diningHall.getBreakfastClosing()))) {
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
            MenuFragment menuFragment = new MenuFragment(MenuFragment.FoodType.DiningHall);
            Bundle bundle = new Bundle(1);

            // If late night exists in this dining hall, add it; otherwise, leave it out.
            if (Arrays.asList(LIMITED_LOCATIONS).contains(diningHall.getName())) {
                switch (position) {
                    case 0:
                        bundle.putString("whichMenu", "Breakfast");
                        break;
                    case 1:
                        bundle.putString("whichMenu", "Lunch");
                        break;
                    case 2:
                        bundle.putString("whichMenu", "LimitedL");
                        break;
                    case 3:
                        bundle.putString("whichMenu", "Dinner");
                        break;
                    case 4:
                        bundle.putString("whichMenu", "LimitedD");
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
            if (Arrays.asList(LIMITED_LOCATIONS).contains(diningHall.getName())) {
                return 5;
            } else {
                return 3;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Only set up a Late Night option if it exists.
            if (Arrays.asList(LIMITED_LOCATIONS).contains(diningHall.getName())) {
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

    private class DownloadImageThread extends Thread {

        ImageView headerView;
        String url;

        private DownloadImageThread(ImageView headerView, String url) {
            this.headerView = headerView;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                InputStream input = new java.net.URL(url).openStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(input);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        headerView.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception e) {

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    headerView.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}