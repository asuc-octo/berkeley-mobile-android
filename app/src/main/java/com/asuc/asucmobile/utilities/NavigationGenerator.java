package com.asuc.asucmobile.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.MainMenuAdapter;
import com.asuc.asucmobile.domain.fragments.BlankFragment;
import com.asuc.asucmobile.domain.fragments.FoodFragment;
import com.asuc.asucmobile.domain.fragments.GymFragment;
import com.asuc.asucmobile.domain.fragments.LibraryFragment;
import com.asuc.asucmobile.domain.fragments.ResourceFragment;
//import com.asuc.asucmobile.domain.fragments.StartStopSelectFragment;
import com.asuc.asucmobile.domain.main.MainActivity;
import com.asuc.asucmobile.domain.fragments.MapsFragment;
import com.asuc.asucmobile.domain.models.Category;

// non-firebase stuff
import com.asuc.asucmobile.domain.fragments.PlaceholderFragment;

public class NavigationGenerator {

    private static final Category[] SECTIONS = new Category[]{
            new Category(R.drawable.beartransit, "BearTransit") {

                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    if (MapsFragment.getInstance() == null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, new MapsFragment())
                                .commit();
                    }
                    else{
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, MapsFragment.getInstance())
                                .commit();
                    }
                }
            },
            new Category(R.drawable.dining_hall, "Food") {
                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new FoodFragment())
                            .commit();
                }
            },
            new Category(R.drawable.library, "Libraries") {
                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new LibraryFragment())
                            .commit();
                }
            },
            new Category(R.drawable.gym, "Gyms") {
                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new GymFragment())
                            .commit();
                }
            },
            new Category(R.drawable.calendar, "Calendar") {
                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new PlaceholderFragment())
                            .commit();
                }
            }
            ,
            new Category(R.drawable.resources, "Resources") {
                @Override
                public void loadFragment(FragmentManager fragmentManager) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new ResourceFragment())
                            .commit();
                }
            }
    };

    private static NavigationGenerator instance;

    private MainMenuAdapter adapter;

    public static NavigationGenerator getInstance() {
        if (instance == null) {
            instance = new NavigationGenerator();
        }
        return instance;
    }

    /**
     * For Fragments with Toolbars
     */
    public static void generateToolbarMenuButton(@NonNull final Activity activity, Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.navi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(activity);
            }
        });
    }

    /**
     * For Fragments without Toolbars
     */
    public static void generateToolbarMenuButton(@NonNull final Activity activity, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(activity);
            }
        });
    }

    public void generateMenu(@NonNull final AppCompatActivity activity) {
        // Set the adapter for the list view
        ListView drawerList = (ListView) activity.findViewById(R.id.drawer_list);
        adapter = new MainMenuAdapter(activity, SECTIONS);
        if (drawerList != null) {
            drawerList.setAdapter(adapter);
            drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.findViewById(R.id.content_frame) == null) {
                                Intent i = new Intent(activity, MainActivity.class);
                                i.putExtra("page", position);
                                activity.startActivity(i);
                                activity.finish();
                            } else {
                                adapter.getItem(position).loadFragment(activity.getSupportFragmentManager());
                            }
                        }
                    }, 0);
                }
            });
        }
    }

    public void loadSection(@NonNull AppCompatActivity activity, int index) {
        if (index == -1) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new BlankFragment())
                    .commit();
        } else {
            adapter.getItem(index).loadFragment(activity.getSupportFragmentManager());
        }
    }

    @SuppressLint("all")
    public static void openMenu(@NonNull final Activity activity) {
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @SuppressLint("all")
    public static void closeMenu(@NonNull Activity activity) {
        final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }, 0);
        }
    }

}
