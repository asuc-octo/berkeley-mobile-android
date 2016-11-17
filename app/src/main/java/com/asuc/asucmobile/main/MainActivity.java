package com.asuc.asucmobile.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.CardAdapter;
import com.asuc.asucmobile.controllers.DiningCardController;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.controllers.GymCardController;
import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        // Setup navigation menu.
        NavigationGenerator.getInstance().openMenu(this);
        if (getIntent().getExtras() != null) {
            int page = getIntent().getExtras().getInt("page", 0);
            NavigationGenerator.getInstance().loadSection(this, page);
        } else {
            NavigationGenerator.getInstance().loadSection(this, -1);
        }

        final CardAdapter cardAdapterGyms = new CardAdapter(this);
        final CardAdapter cardAdapterDining = new CardAdapter(this);
        final Activity activity = this;

        GymCardController.getInstance().refreshInBackground(this, new Callback() {

            @Override
            public void onDataRetrieved(Object data) {
                ArrayList cards = (ArrayList) data;
                cardAdapterGyms.setList(cards);
                HorizontalListView listView = (HorizontalListView) findViewById(R.id.listGyms);
                listView.setAdapter(cardAdapterGyms);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Card card = cardAdapterGyms.getItem(i);
                        GymController controller = ((GymController) GymController.getInstance());
                        controller.setCurrentGym((Gym) card.getData());
                        Intent intent = new Intent(activity, OpenGymActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onRetrievalFailed() {
                // Let the user know the app wasn't able to connect.
                Log.e("GG", "FAIL MOFO");
            }

        });

        DiningCardController.getInstance().refreshInBackground(this, new Callback() {

            @Override
            public void onDataRetrieved(Object data) {
                ArrayList cards = (ArrayList) data;
                cardAdapterDining.setList(cards);
                HorizontalListView listView = (HorizontalListView) findViewById(R.id.listDiningHalls);
                listView.setAdapter(cardAdapterDining);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Card card = cardAdapterDining.getItem(i);
                        DiningController controller = ((DiningController) DiningController.getInstance());
                        controller.setCurrentDiningHall((DiningHall) card.getData());
                        Intent intent = new Intent(activity, OpenDiningHallActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onRetrievalFailed() {
                // Let the user know the app wasn't able to connect.
                Log.e("GG", "FAIL MOFO");
            }

        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        LiveBusActivity.stopBusTracking();
    }

}
