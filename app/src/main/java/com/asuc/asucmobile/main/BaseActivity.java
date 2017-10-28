package com.asuc.asucmobile.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.utilities.NavigationGenerator;

public class BaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState, int layout) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        NavigationGenerator.getInstance().generateMenu(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationGenerator.getInstance().generateMenu(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    public void setupToolbar(String title, boolean hasBackButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
            if (hasBackButton) {
                toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        }
    }

}
