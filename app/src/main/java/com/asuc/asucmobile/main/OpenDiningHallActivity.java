package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodAdapter;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class OpenDiningHallActivity extends AppCompatActivity {

    private DiningHall diningHall;
    private MenuFragment mMenuFragment;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        diningHall = ((DiningController) DiningController.getInstance(this)).getCurrentDiningHall();
        if (diningHall == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_open_dining_hall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(diningHall.getName());
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.off_white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        Date currentTime = new Date();
        if (diningHall.isDinnerOpen() ||
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dining, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.sortAZ) {
            FoodAdapter foodAdapter = mMenuFragment.getmAdapter();
            DiningHall diningHall = mMenuFragment.getmDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            if (arrayListBreakfast.size() != 0) Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByAZ());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            if (arrayListLunch.size() != 0) Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByAZ());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            if (arrayListDinner.size() != 0) Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByAZ());
            if (foodAdapter != null) foodAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.sortVegetarian) {
            FoodAdapter foodAdapter = mMenuFragment.getmAdapter();
            DiningHall diningHall = mMenuFragment.getmDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            if (arrayListBreakfast.size() != 0) Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            if (arrayListLunch.size() != 0) Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            if (arrayListDinner.size() != 0) Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            if (foodAdapter != null) foodAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.sortVegan) {
            FoodAdapter foodAdapter = mMenuFragment.getmAdapter();
            DiningHall diningHall = mMenuFragment.getmDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            if (arrayListBreakfast.size() != 0) Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByVegan());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            if (arrayListLunch.size() != 0) Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByVegan());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            if (arrayListDinner.size() != 0) Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByVegan());
            if (foodAdapter != null) foodAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.sortFavorites) {
            FoodAdapter foodAdapter = mMenuFragment.getmAdapter();
            DiningHall diningHall = mMenuFragment.getmDiningHall();
            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            if (arrayListBreakfast.size() != 0) Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByFavorite());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            if (arrayListLunch.size() != 0) Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByFavorite());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            if (arrayListDinner.size() != 0) Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByFavorite());
            if (foodAdapter != null) foodAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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

            mMenuFragment = menuFragment;

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

    /**
     * getDiningHall() returns the DiningHall associated with the latest instance of this Activity.
     * Used to obtain the DiningHall from the MenuFragment.
     *
     * @return DiningHall of this Activity.
     */
    public DiningHall getDiningHall() {
        return diningHall;
    }

}
