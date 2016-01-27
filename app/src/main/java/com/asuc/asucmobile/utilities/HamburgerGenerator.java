package com.asuc.asucmobile.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.main.DiningHallActivity;
import com.asuc.asucmobile.main.GymActivity;
import com.asuc.asucmobile.main.LibraryActivity;
import com.asuc.asucmobile.main.StopActivity;
import com.asuc.asucmobile.models.Category;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by Victor on 1/26/16.
 */
public class HamburgerGenerator {
    public static void generateMenu(final Activity activity) {
        SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.activity_main);

        Category[] menuItems = new Category[] {
                new Category(activity.getResources().getDrawable(R.drawable.dining_hall_blue), "DINING HALL MENUS", new Intent(activity, DiningHallActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.library_blue), "LIBRARY HOURS", new Intent(activity, LibraryActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.gym_blue), "GYM HOURS", new Intent(activity, GymActivity.class)),
                new Category(activity.getResources().getDrawable(R.drawable.transit_blue), "BEARTRANSIT", new Intent(activity, StopActivity.class))
        };

        ListView menuList = (ListView) activity.findViewById(R.id.main_menu);

        final MainMenuAdapter adapter = new MainMenuAdapter(activity, menuItems);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.startActivity(adapter.getItem(i).getIntent());
            }
        });
    }
}
