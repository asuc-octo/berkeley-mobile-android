package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiningController implements Controller {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    private static DiningController instance;

    private Context context;
    private ArrayList<DiningHall> diningHalls;
    private Callback callback;

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
        if (array == null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onRetrievalFailed();
                }
            });
            return;
        }

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
                            JSONObject foodJSON = breakfastJSON.getJSONObject(j);
                            breakfastMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type"),
                                    foodJSON.getString("calories")
                            ));
                        }

                        JSONArray lunchJSON = diningHall.getJSONArray("lunch_menu");
                        ArrayList<FoodItem> lunchMenu = new ArrayList<FoodItem>();
                        for (int j = 0; j < lunchJSON.length(); j++) {
                            JSONObject foodJSON = lunchJSON.getJSONObject(j);
                            lunchMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type"),
                                    foodJSON.getString("calories")
                            ));
                        }

                        JSONArray dinnerJSON = diningHall.getJSONArray("dinner_menu");
                        ArrayList<FoodItem> dinnerMenu = new ArrayList<FoodItem>();
                        for (int j = 0; j < dinnerJSON.length(); j++) {
                            JSONObject foodJSON = dinnerJSON.getJSONObject(j);
                            dinnerMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("food_type"),
                                    foodJSON.getString("calories")
                            ));
                        }

                        String openingString = diningHall.getString("breakfast_open");
                        String closingString = diningHall.getString("breakfast_close");

                        Date breakfastOpening = null;
                        Date breakfastClosing = null;
                        if (!openingString.equals("null")) {
                            breakfastOpening = DATE_FORMAT.parse(openingString);
                        }
                        if (!closingString.equals("null")) {
                            breakfastClosing = DATE_FORMAT.parse(closingString);
                        }
                        
                        openingString = diningHall.getString("lunch_open");
                        closingString = diningHall.getString("lunch_close");
                        
                        Date lunchOpening = null;
                        Date lunchClosing = null;
                        if (!openingString.equals("null")) {
                            lunchOpening = DATE_FORMAT.parse(openingString);
                        }
                        if (!closingString.equals("null")) {
                            lunchClosing = DATE_FORMAT.parse(closingString);
                        }

                        openingString = diningHall.getString("dinner_open");
                        closingString = diningHall.getString("dinner_close");

                        Date dinnerOpening = null;
                        Date dinnerClosing = null;
                        if (!openingString.equals("null")) {
                            dinnerOpening = DATE_FORMAT.parse(openingString);
                        }
                        if (!closingString.equals("null")) {
                            dinnerClosing = DATE_FORMAT.parse(closingString);
                        }

                        openingString = diningHall.getString("late_night_open");
                        closingString = diningHall.getString("late_night_close");

                        Date lateNightOpening = null;
                        Date lateNightClosing = null;
                        if (!openingString.equals("null")) {
                            lateNightOpening = DATE_FORMAT.parse(openingString);
                        }
                        if (!closingString.equals("null")) {
                            lateNightClosing = DATE_FORMAT.parse(closingString);
                        }

                        String imageUrl = diningHall.getString("image_link");

                        diningHalls.add(new DiningHall(
                                id, name, breakfastMenu, lunchMenu, dinnerMenu, breakfastOpening,
                                breakfastClosing, lunchOpening, lunchClosing, dinnerOpening,
                                dinnerClosing, lateNightOpening, lateNightClosing, imageUrl
                        ));
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(diningHalls);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onRetrievalFailed();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void refreshInBackground(Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl("http://asuc-mobile.herokuapp.com/api/dining_halls", "dining_halls", DiningController.getInstance(context));
    }

}
