package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.MainMenuAdapter;
import com.asuc.asucmobile.models.Category;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Category[] menuItems = new Category[] {
                new Category(getResources().getDrawable(R.drawable.dining_hall_blue), "DINING HALL MENUS", new Intent(this, DiningHallActivity.class)),
                new Category(getResources().getDrawable(R.drawable.library_blue), "LIBRARY HOURS", new Intent(this, LibraryActivity.class)),
                new Category(getResources().getDrawable(R.drawable.gym_blue), "GYM HOURS", new Intent(this, GymActivity.class))
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

}
