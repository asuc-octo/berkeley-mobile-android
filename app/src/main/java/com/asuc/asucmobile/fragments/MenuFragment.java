package com.asuc.asucmobile.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodAdapter;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        ListView foodMenu = (ListView) v.findViewById(R.id.food_menu);
        TextView emptyListView = (TextView) v.findViewById(R.id.empty_list);

        emptyListView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "young.ttf"));

        DiningHall diningHall = ((OpenDiningHallActivity) getActivity()).getDiningHall();
        ArrayList<FoodItem> foodItems;
        String whichMenu = getArguments().getString("whichMenu");
        if (whichMenu.equals("Breakfast")) {
            foodItems = diningHall.getBreakfastMenu();
        } else if (whichMenu.equals("Lunch")) {
            foodItems = diningHall.getLunchMenu();
        } else {
            foodItems = diningHall.getDinnerMenu();
        }

        FoodAdapter adapter = new FoodAdapter(getActivity(), foodItems);
        foodMenu.setAdapter(adapter);

        if (foodItems.size() == 0) {
            foodMenu.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }

        return v;
    }

}
