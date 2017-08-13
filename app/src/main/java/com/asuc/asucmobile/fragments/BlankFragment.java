package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.CardAdapter;
import com.asuc.asucmobile.controllers.DiningCardController;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymCardController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.main.MainActivity;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.main.OpenGymActivity;
import com.asuc.asucmobile.models.Card;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.models.G.Gym;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;

public class BlankFragment extends Fragment {

    private static final String APP_NAME = "Berkeley Mobile";

    private CardAdapter cardAdapterGyms;
    private CardAdapter cardAdapterDining;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        NavigationGenerator.generateToolbarMenuButton(getActivity(), toolbar);
        toolbar.setTitle(APP_NAME);

        cardAdapterGyms = new CardAdapter(getContext());
        cardAdapterDining = new CardAdapter(getContext());
        refresh();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refresh() {
        GymCardController.getInstance().refreshInBackground(getContext(), new Callback() {

            @Override
            public void onDataRetrieved(Object data) {
                ArrayList cards = (ArrayList) data;
                cardAdapterGyms.setList(cards);
                HorizontalListView listView = (HorizontalListView) getView().findViewById(R.id.listGyms);
                listView.setAdapter(cardAdapterGyms);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Card card = cardAdapterGyms.getItem(i);
                        GymController controller = ((GymController) GymController.getInstance());
                        controller.setCurrentGym((Gym) card.getData());
                        Intent intent = new Intent(getActivity(), OpenGymActivity.class);
                        startActivity(intent);
                    }
                });
                TextView gymHallText = (TextView) getView().findViewById(R.id.gymText);

                gymHallText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, new GymFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }

            @Override
            public void onRetrievalFailed() {
                // Let the user know the app wasn't able to connect.
                Log.e("GG", "FAIL MOFO");
            }

        });

        DiningCardController.getInstance().refreshInBackground(getContext(), new Callback() {

            @Override
            public void onDataRetrieved(Object data) {
                ArrayList cards = (ArrayList) data;
                cardAdapterDining.setList(cards);
                HorizontalListView listView = (HorizontalListView) getView().findViewById(R.id.listDiningHalls);
                listView.setAdapter(cardAdapterDining);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Card card = cardAdapterDining.getItem(i);
                        DiningController controller = ((DiningController) DiningController.getInstance());
                        controller.setCurrentDiningHall((DiningHall) card.getData());
                        Intent intent = new Intent(getActivity(), OpenDiningHallActivity.class);
                        startActivity(intent);
                    }
                });

                TextView diningHallText = (TextView) getView().findViewById(R.id.diningHallText);

                diningHallText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, new DiningHallFragment())
                                .addToBackStack(null)
                                .commit();
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

}
