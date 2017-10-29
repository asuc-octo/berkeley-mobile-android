package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rustie on 9/28/17.
 */

public class CafeController implements Controller{

    private static final String TAG = "CafeController";
    private static final String URL = BASE_URL + "/cafes";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");

    private static CafeController instance;

    private ArrayList<Cafe> cafes;
    private Callback callback;
    private Cafe currentCafe;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new CafeController();
        }
        return instance;
    }

    private CafeController() {
        cafes = new ArrayList<>();
    }


    @Override
    public void setResources(@NonNull final Context context, final JSONArray array) {
        if (array == null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onRetrievalFailed();
                }
            });
            return;
        }
        cafes.clear();

        /*
         *  Parsing JSON data into models is put into a background thread so that the UI thread
         *  won't lag.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "RUANNANA");
                try {

                    // for each entry in cafes
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cafe = array.getJSONObject(i);
                        String id = cafe.getString("id");
                        String name = cafe.getString("name");
                        String imageLink = cafe.getString("image_link");
                        JSONArray menus = cafe.getJSONArray("menus");

                        // breakfast first
                        JSONArray breakfastJSON = menus.getJSONObject(0).getJSONArray("menu_items");

                        ArrayList<FoodItem> breakfastMenu = new ArrayList<>();
                        for (int j = 0; j < breakfastJSON.length(); j++) {
                            JSONObject foodJSON = breakfastJSON.getJSONObject(j);

                            // get the food types
                            ArrayList<String> foodTypes = new ArrayList<>();
                            if (foodJSON.has("food_type")) {
                                JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                for (int k = 0; k < foodTypesArray.length(); k++) {
                                    foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                }
                            }

                            breakfastMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("calories"),
                                    foodJSON.optDouble("cost"),
                                    foodTypes.toString()

                            ));
                        }
                        Collections.sort(breakfastMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));


                        // lunch and dinner next
                        JSONArray lunchDinnerJSON = menus.getJSONObject(1).getJSONArray("menu_items");
                        ArrayList<FoodItem> lunchDinnerMenu = new ArrayList<>();
                        for (int j = 0; j < lunchDinnerJSON.length(); j++) {
                            JSONObject foodJSON = lunchDinnerJSON.getJSONObject(j);
                            // get the food types
                            ArrayList<String> foodTypes = new ArrayList<>();
                            if (foodJSON.has("food_type")) {
                                JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                for (int k = 0; k < foodTypesArray.length(); k++) {
                                    foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                }
                            }
                            lunchDinnerMenu.add(new FoodItem(
                                    foodJSON.getString("id"),
                                    foodJSON.getString("name"),
                                    foodJSON.getString("calories"),
                                    foodJSON.optDouble("cost"),
                                    foodTypes.toString()
                            ));
                        }
                        Collections.sort(lunchDinnerMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        //TODO: do something about opening and closing times
                        // opening and closing times
                        long tmpDate;
                        String openingString = menus.getJSONObject(0).getString("start_time");
                        String closingString = menus.getJSONObject(0).getString("end_time");

                        Date breakfastOpening = null;
                        Date breakfastClosing = null;
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            breakfastOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            breakfastClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }

                        Date lunchDinnerOpening = null;
                        Date lunchDinnerClosing = null;
                        openingString = menus.getJSONObject(1).getString("start_time");
                        closingString = menus.getJSONObject(1).getString("end_time");
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            lunchDinnerOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            lunchDinnerClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }

                        cafes.add(new Cafe(id, name, breakfastMenu, lunchDinnerMenu, breakfastOpening,
                                breakfastClosing, lunchDinnerOpening, lunchDinnerClosing, imageLink));



                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(cafes);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
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
    public void refreshInBackground(@NonNull Context context, Callback callback) {
        this.callback = callback;
        JSONUtilities.readJSONFromUrl(context, URL, "cafes", CafeController.getInstance());
    }

    public void setCurrentCafe(Cafe cafe) {
        currentCafe = cafe;
    }

    public Cafe getCurrentCafe() {
        return currentCafe;
    }
}
