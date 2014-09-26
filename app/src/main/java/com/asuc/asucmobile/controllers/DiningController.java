package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiningController implements Controller {

    private static DiningController instance;

    private Context context;
    private ArrayList<DiningHall> diningHalls;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new DiningController();
        }

        instance.context = context;

        return instance;
    }

    public DiningController() {
        diningHalls = new ArrayList<DiningHall>();
    }

    @Override
    public void setResources(final JSONArray array) {
        diningHalls.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject diningHall = array.getJSONObject(i);
                        String id = diningHall.getString("id");
                        String name = diningHall.getString("name");

                        JSONArray breakfastJSON = diningHall.getJSONArray("breakfast_menu");
                        ArrayList<FoodItem> breakfastMenu = new ArrayList<FoodItem>();
                        for (int j = 0; j < breakfastJSON.length(); j++) {
                            JSONObject foodJSON = breakfastJSON.getJSONObject(i);
                            breakfastMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type")
                            ));
                        }

                        JSONArray lunchJSON = diningHall.getJSONArray("lunch_menu");
                        ArrayList<FoodItem> lunchMenu = new ArrayList<FoodItem>();
                        for (int j = 0; j < lunchJSON.length(); j++) {
                            JSONObject foodJSON = lunchJSON.getJSONObject(i);
                            lunchMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type")
                            ));
                        }

                        JSONArray dinnerJSON = diningHall.getJSONArray("dinner_menu");
                        ArrayList<FoodItem> dinnerMenu = new ArrayList<FoodItem>();
                        for (int j = 0; j < dinnerJSON.length(); j++) {
                            JSONObject foodJSON = dinnerJSON.getJSONObject(i);
                            dinnerMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type")
                            ));
                        }

                        diningHalls.add(new DiningHall(
                                id, name, breakfastMenu, lunchMenu, dinnerMenu
                        ));
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateUI() {
        // TODO: Update necessary UI components with Dining Hall info.
    }

}
