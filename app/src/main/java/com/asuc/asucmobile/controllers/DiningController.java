package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class DiningController implements Controller {

    private static final String TAG = "DiningController";
    private static final String URL = BASE_URL + "/dining_halls";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");
    private static final String[] HAS_LATE_NIGHT = {"Crossroads","Foothill"};

    private static DiningController instance;

    private ArrayList<DiningHall> diningHalls;
    private Callback callback;
    private DiningHall currentDiningHall;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new DiningController();
        }
        return instance;
    }

    private DiningController() {
        diningHalls = new ArrayList<>();
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

                        // dining hall info
                        JSONObject diningHall = array.getJSONObject(i);
                        String id = diningHall.getString("id");
                        String name = diningHall.getString("name");



                        // breakfast stuff
                        ArrayList<FoodItem> breakfastMenu = new ArrayList<>();
                        try {
                            JSONArray breakfastJSON = diningHall.getJSONArray("breakfast_menu");
                            for (int j = 0; j < breakfastJSON.length(); j++) {
                                JSONObject foodJSON = breakfastJSON.getJSONObject(j);
                                // get the food types
                                ArrayList<String> foodTypes = new ArrayList<>();

                                try {
                                    if (foodJSON.has("food_type")) {
                                        JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                        for (int k = 0; k < foodTypesArray.length(); k++) {
                                            foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    breakfastMenu.add(new FoodItem(
                                            foodJSON.getString("id"),
                                            foodJSON.getString("name"),
                                            foodJSON.getString("calories"),
                                            foodJSON.optDouble("cost"),
                                            foodTypes
                                    ));
                                }
                            }
                            Collections.sort(breakfastMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }


                        // lunch stuff
                        ArrayList<FoodItem> lunchMenu = new ArrayList<>();
                        try {
                            JSONArray lunchJSON = diningHall.getJSONArray("lunch_menu");
                            for (int j = 0; j < lunchJSON.length(); j++) {
                                JSONObject foodJSON = lunchJSON.getJSONObject(j);
                                // get the food types
                                ArrayList<String> foodTypes = new ArrayList<>();
                                try {
                                    if (foodJSON.has("food_type")) {
                                        JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                        for (int k = 0; k < foodTypesArray.length(); k++) {
                                            foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    lunchMenu.add(new FoodItem(
                                            foodJSON.getString("id"),
                                            foodJSON.getString("name"),
                                            foodJSON.getString("calories"),
                                            foodJSON.optDouble("cost"),
                                            foodTypes
                                    ));
                                }

                            }
                            Collections.sort(lunchMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }



                        // dinner stuff
                        ArrayList<FoodItem> dinnerMenu = new ArrayList<>();
                        try {
                            JSONArray dinnerJSON = diningHall.getJSONArray("dinner_menu");
                            for (int j = 0; j < dinnerJSON.length(); j++) {
                                JSONObject foodJSON = dinnerJSON.getJSONObject(j);
                                // get the food types
                                ArrayList<String> foodTypes = new ArrayList<>();
                                try {
                                    if (foodJSON.has("food_type")) {
                                        JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                        for (int k = 0; k < foodTypesArray.length(); k++) {
                                            foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    dinnerMenu.add(new FoodItem(
                                            foodJSON.getString("id"),
                                            foodJSON.getString("name"),
                                            foodJSON.getString("calories"),
                                            foodJSON.optDouble("cost"),
                                            foodTypes
                                    ));
                                }
                            }
                            Collections.sort(dinnerMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        ArrayList<FoodItem> limitedLunchMenu = new ArrayList<>();
                        ArrayList<FoodItem> limitedDinnerMenu = new ArrayList<>();


                        /*
                          Limited is treated specially. Because dining halls without Limited
                          have its menu as "null" rather than an empty list, we first ask if the
                          dining hall has late night, and then apply the appropriate actions.
                         */
                        if (Arrays.asList(HAS_LATE_NIGHT).contains(name)) {
                            try {
                                JSONArray lateNightJSON = diningHall.getJSONArray("limited_lunch_menu");
                                for (int j = 0; j < lateNightJSON.length(); j++) {
                                    JSONObject foodJSON = lateNightJSON.getJSONObject(j);
                                    // get the food types
                                    ArrayList<String> foodTypes = new ArrayList<>();
                                    try {
                                        if (foodJSON.has("food_type")) {
                                            JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                            for (int k = 0; k < foodTypesArray.length(); k++) {
                                                foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                            }

                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        limitedLunchMenu.add(new FoodItem(
                                                foodJSON.getString("id"),
                                                foodJSON.getString("name"),
                                                foodJSON.getString("calories"),
                                                foodJSON.optDouble("cost"),
                                                foodTypes
                                        ));
                                    }
                                }
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }

                        }
                        Collections.sort(limitedLunchMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        if (Arrays.asList(HAS_LATE_NIGHT).contains(name)) {
                            try {
                                JSONArray lateNightJSON = diningHall.getJSONArray("limited_dinner_menu");
                                for (int j = 0; j < lateNightJSON.length(); j++) {
                                    JSONObject foodJSON = lateNightJSON.getJSONObject(j);
                                    // get the food types
                                    ArrayList<String> foodTypes = new ArrayList<>();
                                    try {
                                        if (foodJSON.has("food_type")) {
                                            JSONArray foodTypesArray = foodJSON.getJSONArray("food_type");
                                            for (int k = 0; k < foodTypesArray.length(); k++) {
                                                foodTypes.add(foodTypesArray.getString(k).toUpperCase());
                                            }
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    } finally {
                                        limitedDinnerMenu.add(new FoodItem(
                                                foodJSON.getString("id"),
                                                foodJSON.getString("name"),
                                                foodJSON.getString("calories"),
                                                foodJSON.optDouble("cost"),
                                                foodTypes
                                        ));
                                    }

                                }
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        }

                        Collections.sort(limitedDinnerMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));


                        long tmpDate;
                        String openingString = diningHall.getString("breakfast_open");
                        String closingString = diningHall.getString("breakfast_close");
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
                        openingString = diningHall.getString("lunch_open");
                        closingString = diningHall.getString("lunch_close");
                        Date lunchOpening = null;
                        Date lunchClosing = null;
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            lunchOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            lunchClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        openingString = diningHall.getString("dinner_open");
                        closingString = diningHall.getString("dinner_close");
                        Date dinnerOpening = null;
                        Date dinnerClosing = null;
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            dinnerOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            dinnerClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }


                        openingString = diningHall.getString("limited_lunch_open");
                        closingString = diningHall.getString("limited_lunch_close");
                        Date limitedLunchOpening = null;
                        Date limitedLunchClosing = null;
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            limitedLunchOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            limitedLunchClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }

                        openingString = diningHall.getString("limited_dinner_open");
                        closingString = diningHall.getString("limited_dinner_close");
                        Date limitedDinnerOpening = null;
                        Date limitedDinnerClosing = null;
                        if (!openingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(openingString).getTime();
                            limitedDinnerOpening = new Date(tmpDate + PST.getOffset(tmpDate));
                        }
                        if (!closingString.equals("null")) {
                            tmpDate = DATE_FORMAT.parse(closingString).getTime();
                            limitedDinnerClosing = new Date(tmpDate + PST.getOffset(tmpDate));
                        }


                        String imageUrl = diningHall.getString("image_link");
                        diningHalls.add(new DiningHall(
                                id, name, breakfastMenu, lunchMenu, dinnerMenu, limitedLunchMenu, limitedDinnerMenu,
                                breakfastOpening, breakfastClosing, lunchOpening, lunchClosing,
                                dinnerOpening, dinnerClosing, limitedLunchOpening, limitedLunchClosing, limitedDinnerOpening, limitedDinnerClosing,
                                imageUrl
                        ));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataRetrieved(diningHalls);
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
        JSONUtilities.readJSONFromUrl(context, URL, "dining_halls", DiningController.getInstance());
    }

    public void setCurrentDiningHall(DiningHall diningHall) {
        currentDiningHall = diningHall;
    }

    public DiningHall getCurrentDiningHall() {
        return currentDiningHall;
    }

}