package com.asuc.asucmobile.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.FoodAdapter;
import com.asuc.asucmobile.main.OpenDiningHallActivity;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.views.ImageHeaderView;
import com.nirhart.parallaxscroll.views.ParallaxListView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MenuFragment extends Fragment {

    private static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        ParallaxListView foodMenu = (ParallaxListView) v.findViewById(R.id.food_menu);
        TextView emptyListView = (TextView) v.findViewById(R.id.empty_list);

        DiningHall diningHall = ((OpenDiningHallActivity) getActivity()).getDiningHall();

        if (diningHall == null) {
            getActivity().finish();
            return v;
        }

        ImageHeaderView header = new ImageHeaderView(getActivity());
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        new DownloadImageThread(progressBar, header, diningHall.getImageUrl()).start();

        String whichMenu = getArguments().getString("whichMenu");
        String opening;
        String closing;

        try {
            ArrayList<FoodItem> foodItems;
            boolean isOpen;
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
                    default:
                        foodItems = diningHall.getDinnerMenu();
                        opening = HOURS_FORMAT.format(diningHall.getDinnerOpening());
                        closing = HOURS_FORMAT.format(diningHall.getDinnerClosing());
                        isOpen = diningHall.isDinnerOpen();
                }

                if (foodItems.size() == 0) {
                    emptyListView.setText("No " + whichMenu + " Today!");

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

                    header.setText(spannableHeader);

                    FoodAdapter adapter = new FoodAdapter(getActivity(), foodItems);
                    foodMenu.setAdapter(adapter);
                    foodMenu.addParallaxedHeaderView(header);
                }
            }
        } catch (Exception e) { // Catch a null exception, meaning that there is no menu for this time slot.
            emptyListView.setText("No " + whichMenu + " Today!");

            foodMenu.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private class DownloadImageThread extends Thread {

        ProgressBar progressBar;
        ImageHeaderView headerView;
        String url;

        public DownloadImageThread(ProgressBar progressBar, ImageHeaderView headerView, String url) {
            this.progressBar = progressBar;
            this.headerView = headerView;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                InputStream input = new java.net.URL(url).openStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(input);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                        if (bitmap != null) {
                            headerView.setImage(bitmap);
                            headerView.setVisibility(View.VISIBLE);
                        } else {
                            headerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (Exception e) {
                // Don't worry about it!
            }
        }

    }

}
