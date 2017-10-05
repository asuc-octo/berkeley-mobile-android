package com.asuc.asucmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.CardAdapter;
import com.asuc.asucmobile.controllers.Controller;
import com.asuc.asucmobile.controllers.DiningController;
import com.asuc.asucmobile.controllers.GymController;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.main.OpenGymActivity;
import com.asuc.asucmobile.models.Cardable;
import com.asuc.asucmobile.models.DiningHalls;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.models.Gyms;
import com.asuc.asucmobile.models.Gyms.Gym;
import com.asuc.asucmobile.utilities.NavigationGenerator;
import com.devsmart.android.ui.HorizontalListView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
        NavigationGenerator.closeMenu(getActivity());
    }

    public void refresh() {
        DiningController.cService diningController = Controller.retrofit.create(DiningController.cService.class);
        Call<DiningHalls> diningHallsCall = diningController.getData();
        diningHallsCall.enqueue(new retrofit2.Callback<DiningHalls>() {
            @Override
            public void onResponse(Call<DiningHalls> call, Response<DiningHalls> response) {
                List<DiningHall> diningHalls = DiningController.parse(response.body(), getContext());
                cardAdapterDining.setList(diningHalls);
                HorizontalListView listView = (HorizontalListView) getView().findViewById(R.id.listDiningHalls);
                listView.setAdapter(cardAdapterDining);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cardable card = cardAdapterDining.getItem(i);
                        DiningController.setCurrentDiningHall((DiningHall) card);
                        Intent intent = new Intent(getActivity(), OpenDiningHallActivity.class);
                        startActivity(intent);
                    }
                });
                TextView diningHallText = (TextView) getView().findViewById(R.id.diningHallText);

                diningHallText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, new DiningHallFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
            @Override
            public void onFailure(Call<DiningHalls> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });

        GymController.cService gymController = Controller.retrofit.create(GymController.cService.class);
        Call<Gyms> gymsCall = gymController.getData();
        gymsCall.enqueue(new retrofit2.Callback<Gyms>() {
            @Override
            public void onResponse(Call<Gyms> call, Response<Gyms> response) {
                List<Gym> gyms = GymController.parse(response.body());
                cardAdapterGyms.setList(gyms);
                HorizontalListView listView = (HorizontalListView) getView().findViewById(R.id.listGyms);
                listView.setAdapter(cardAdapterGyms);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cardable card = cardAdapterGyms.getItem(i);
                        GymController.setCurrentGym((Gym) card);
                        Intent intent = new Intent(getActivity(), OpenGymActivity.class);
                        startActivity(intent);
                    }
                });
                TextView gymText = (TextView) getView().findViewById(R.id.gymText);

                gymText.setOnClickListener(new View.OnClickListener() {
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
            public void onFailure(Call<Gyms> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to retrieve data, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
