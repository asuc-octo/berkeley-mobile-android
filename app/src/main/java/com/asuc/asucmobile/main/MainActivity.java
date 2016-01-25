package com.asuc.asucmobile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.models.Category;
import com.flurry.android.FlurryAgent;


public class MainActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.onStartSession(this, "4VPTT49FCCKH7Z2NVQ26");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.updated_dining), "DINING HALL MENUS", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.updated_library), "LIBRARY HOURS", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.updated_gym), "GYM HOURS", new Intent(this, GymActivity.class)),
                new Category(getResources().getDrawable(R.drawable.updated_bus), "BEARTRANSIT", new Intent(this, StopActivity.class))
        };

        ListView menuList = (ListView) findViewById(R.id.main_menu);

        final MainMenuAdapter adapter = new MainMenuAdapter(this, menuItems);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(adapter.getItem(i).getIntent());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
