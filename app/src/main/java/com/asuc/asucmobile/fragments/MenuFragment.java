package com.asuc.asucmobile.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.asuc.asucmobile.utilities.CustomComparators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MenuFragment extends Fragment {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private static final String LATE_NIGHT_OPEN = "10:00 PM";
    private static final String LATE_NIGHT_CLOSE = "2:00 AM";

    private static ArrayList<FoodAdapter> adapters = new ArrayList<>();
    private static DiningHall diningHall;
    private static ArrayList<FoodItem> foodItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view and retrieve dining hall info.
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        DiningHall diningHall = ((OpenDiningHallActivity) getActivity()).getDiningHall();
        if (diningHall == null) {
            getActivity().finish();
            return v;
        }

        // Get references to views.
        ListView foodMenu = (ListView) v.findViewById(R.id.food_menu);
        TextView emptyListView = (TextView) v.findViewById(R.id.empty_list);
        View header = getActivity().getLayoutInflater().inflate(R.layout.header_image, foodMenu, false);
        TextView headerHours = (TextView) header.findViewById(R.id.header_text);


        // Populate menu views.
        String whichMenu = getArguments().getString("whichMenu");
        String opening;
        String closing;
        try {
            boolean isOpen;
            MenuFragment.diningHall = diningHall;
            if (whichMenu != null) {
                switch (whichMenu) {
                    case "Breakfast":
                        foodItems = diningHall.getBreakfastMenu();

                        opening = HOURS_FORMAT.format(diningHall.getBreakfastOpening());
                        closing = HOURS_FORMAT.format(diningHall.getBreakfastClosing());
                        isOpen = diningHall.isBreakfastOpen();
                        break;
                    case "Lunch":
                        foodItems = diningHall.getLunchMenu();
                        opening = HOURS_FORMAT.format(diningHall.getLunchOpening());
                        closing = HOURS_FORMAT.format(diningHall.getLunchClosing());
                        isOpen = diningHall.isLunchOpen();
                        break;
                    case "LimitedL":
                        if (diningHall.limitedLunchToday()) {
                            foodItems = diningHall.getLimitedLunchMenu();
                        } else {
                            foodItems = null;
                        }
                        if (diningHall.getLimitedLunchOpen() != null) {
                            opening = HOURS_FORMAT.format(diningHall.getLimitedLunchOpen());
                        } else {
                            opening = LATE_NIGHT_OPEN;
                        }
                        if (diningHall.getLimitedLunchClosing() != null) {
                            closing = HOURS_FORMAT.format(diningHall.getLimitedLunchClosing());
                        } else {
                            closing = LATE_NIGHT_CLOSE;
                        }
                        isOpen = diningHall.isLimitedLunchOpen();
                        break;
                    case "LimitedD":
                        if (diningHall.limitedDinnerToday()) {
                            foodItems = diningHall.getLimitedDinnerMenu();
                        } else {
                            foodItems = null;
                        }
                        if (diningHall.getLimitedDinnerOpen() != null) {
                            opening = HOURS_FORMAT.format(diningHall.getLimitedDinnerOpen());
                        } else {
                            opening = LATE_NIGHT_OPEN;
                        }
                        if (diningHall.getLimitedDinnerClosing() != null) {
                            closing = HOURS_FORMAT.format(diningHall.getLimitedDinnerClosing());
                        } else {
                            closing = LATE_NIGHT_CLOSE;
                        }
                        isOpen = diningHall.isLimitedDinnerOpen();
                        break;
                    default:
                        foodItems = diningHall.getDinnerMenu();
                        opening = HOURS_FORMAT.format(diningHall.getDinnerOpening());
                        closing = HOURS_FORMAT.format(diningHall.getDinnerClosing());
                        isOpen = diningHall.isDinnerOpen();
                }
                if (foodItems == null || foodItems.size() == 0) {
                    if(whichMenu.equals("LimitedL"))
                        emptyListView.setText(String.format("No %s Today!", "Continuous Service"));
                    else if(whichMenu.equals("LimitedD"))
                        emptyListView.setText(String.format("No %s Tonight!", "Continuous Service"));
                    else
                        emptyListView.setText(String.format("No %s Today!", whichMenu));
                    foodMenu.setVisibility(View.GONE);
                    emptyListView.setVisibility(View.VISIBLE);
                } else {
                    String headerString = "Hours:  " + opening + " to " + closing + "  ";
                    SpannableString spannableHeader;
                    if (isOpen) {
                        spannableHeader = new SpannableString(headerString + "OPEN");
                        spannableHeader.setSpan(
                                new ForegroundColorSpan(Color.rgb(153, 204, 0)),
                                headerString.length(),
                                headerString.length() + 4,
                                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                        );
                    } else {
                        spannableHeader = new SpannableString(headerString + "CLOSED");
                        spannableHeader.setSpan(
                                new ForegroundColorSpan(Color.rgb(255, 68, 68)),
                                headerString.length(),
                                headerString.length() + 6,
                                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                        );
                    }
                    headerHours.setText(spannableHeader);
                    FoodAdapter adapter = new FoodAdapter(getActivity(), foodItems);
                    MenuFragment.adapters.add(adapter);
                    foodMenu.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Catch a null exception, meaning that there is no menu for this time slot.
            emptyListView.setText(String.format("No %s Today!", whichMenu));
            foodMenu.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }



        return v;
    }


    public static void refreshLists() {
        if (MenuFragment.adapters == null) {
            return;
        }
        for (FoodAdapter adapter : MenuFragment.adapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static DiningHall getDiningHall() {
        return diningHall;
    }

}
