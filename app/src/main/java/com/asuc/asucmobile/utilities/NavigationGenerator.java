package com.asuc.asucmobile.utilities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.main.DiningHallActivity;
import com.asuc.asucmobile.main.GymActivity;
import com.asuc.asucmobile.main.LibraryActivity;
import com.asuc.asucmobile.main.StartStopSelectActivity;
import com.asuc.asucmobile.models.Category;

/**
 * Created by Victor on 3/4/16.
 */
public class NavigationGenerator {

    public static void generateMenu(final Activity activity, Toolbar toolbar) {
        // Set the adapter for the list view
        final DrawerLayout mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) activity.findViewById(R.id.left_drawer);
        Category[] menuItems = new Category[] {
                new Category(activity.getResources().getDrawable(R.drawable.beartransit), "BearTransit", new Intent(activity, StartStopSelectActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.dining_hall), "Dining Halls", new Intent(activity, DiningHallActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.library), "Libraries", new Intent(activity, LibraryActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.gym), "Gyms", new Intent(activity, GymActivity.class))
        };

        final MainMenuAdapter adapter = new MainMenuAdapter(activity, menuItems);

        toolbar.setNavigationIcon(R.drawable.navi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.startActivity(adapter.getItem(i).getIntent());
            }
        });

    }
}
