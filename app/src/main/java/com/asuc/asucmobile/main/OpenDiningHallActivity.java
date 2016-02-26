package com.asuc.asucmobile.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.fragments.MenuFragment;
import com.asuc.asucmobile.models.Category;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.SerializableUtilities;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class OpenDiningHallActivity extends AppCompatActivity {

    private DiningHall diningHall;

    private static final String[] HAS_LATE_NIGHT = {"Crossroads","Foothill"};

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

        ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject(this);
        if (listOfFavorites == null) {
            listOfFavorites = new ListOfFavorites();
            SerializableUtilities.saveObject(this, listOfFavorites);
        }

        setContentView(R.layout.activity_open_dining_hall);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.beartransit), "BearTransit", new Intent(this, StartStopSelectActivity.class)),
                new Category(getResources().getDrawable(R.drawable.dining_hall), "Dining Halls", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.library), "Libraries", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.gym), "Gyms", new Intent(this, GymActivity.class))
        };

        final MainMenuAdapter adapter = new MainMenuAdapter(this, menuItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(adapter.getItem(i).getIntent());
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(diningHall.getName());
        setSupportActionBar(toolbar);
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
        if (Arrays.asList(HAS_LATE_NIGHT).contains(diningHall.getName())) {
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

        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        tabStrip.setTextColor(getResources().getColor(R.color.off_white));
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.off_white));
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
        if (id == R.id.sortVegetarian) {
            DiningHall diningHall = MenuFragment.getDiningHall();

            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByVegetarian());
            ArrayList<FoodItem> arrayListLateNight = diningHall.getLateNightMenu();
            Collections.sort(arrayListLateNight, CustomComparators.FacilityComparators.getFoodSortByVegetarian());

            MenuFragment.refreshLists();
            return true;
        }
        if (id == R.id.sortVegan) {
            DiningHall diningHall = MenuFragment.getDiningHall();

            ArrayList<FoodItem> arrayListBreakfast = diningHall.getBreakfastMenu();
            Collections.sort(arrayListBreakfast, CustomComparators.FacilityComparators.getFoodSortByVegan());
            ArrayList<FoodItem> arrayListLunch = diningHall.getLunchMenu();
            Collections.sort(arrayListLunch, CustomComparators.FacilityComparators.getFoodSortByVegan());
            ArrayList<FoodItem> arrayListDinner = diningHall.getDinnerMenu();
            Collections.sort(arrayListDinner, CustomComparators.FacilityComparators.getFoodSortByVegan());
            ArrayList<FoodItem> arrayListLateNight = diningHall.getLateNightMenu();
            Collections.sort(arrayListLateNight, CustomComparators.FacilityComparators.getFoodSortByVegan());

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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MenuFragment menuFragment = new MenuFragment();
            Bundle bundle = new Bundle(1);

            /*
                If late night exists in this dining hall, add it; otherwise, leave it out.
             */
            if (Arrays.asList(HAS_LATE_NIGHT).contains(diningHall.getName())) {
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
                    case 3:
                        bundle.putString("whichMenu", "Late Night");
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
            if (Arrays.asList(HAS_LATE_NIGHT).contains(diningHall.getName())) {
                return 4;
            } else {
                return 3;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*
                Only set up a Late Night option if it exists.
             */
            if (Arrays.asList(HAS_LATE_NIGHT).contains(diningHall.getName())) {
                switch (position) {
                    case 0:
                        return "     Breakfast     ";
                    case 1:
                        return "     Lunch     ";
                    case 2:
                        return "     Dinner     ";
                    case 3:
                        return "     Late Night     ";
                }
            } else {
                switch (position) {
                    case 0:
                        return "     Breakfast     ";
                    case 1:
                        return "     Lunch     ";
                    case 2:
                        return "     Dinner     ";
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

}