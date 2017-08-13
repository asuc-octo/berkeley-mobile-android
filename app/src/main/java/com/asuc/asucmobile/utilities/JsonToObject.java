package com.asuc.asucmobile.utilities;

import android.content.Context;

import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.models.Gyms.Gym;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JsonToObject {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");
    private static final String[] HAS_LATE_NIGHT = {"Crossroads","Foothill"};

    public static Object retrieve(JSONObject jsonObject, String target, Context context) throws JSONException, ParseException {
        switch (target) {
            case "gyms":
                return retrieveGym(jsonObject);
            case "dining_halls":
                return retrieveDining(jsonObject, context);
        }
        return null;
    }

    private static Object retrieveGym(JSONObject jsonObject) throws JSONException, ParseException {
        JSONObject gymJSON = jsonObject;
        int id = gymJSON.getInt("id");
        String name = gymJSON.getString("name");
        String address = gymJSON.getString("address");
        long tmpDate;
        Date opening = null;
        Date closing = null;
        String openingString = gymJSON.getString("opening_time_today");
        String closingString = gymJSON.getString("closing_time_today");
        if (!openingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(openingString).getTime();
            opening = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        if (!closingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(closingString).getTime();
            closing = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        String imageUrl = gymJSON.getString("image_link");
        return new Gym(id, name, address, opening, closing, imageUrl);
    }

    private static Object retrieveDining(JSONObject jsonObject, Context context) throws JSONException, ParseException {
        JSONObject diningHall = jsonObject;
        String id = diningHall.getString("id");
        String name = diningHall.getString("name");
        JSONArray breakfastJSON = diningHall.getJSONArray("breakfast_menu");
        ArrayList<FoodItem> breakfastMenu = new ArrayList<>();
        for (int j = 0; j < breakfastJSON.length(); j++) {
            JSONObject foodJSON = breakfastJSON.getJSONObject(j);
            breakfastMenu.add(new FoodItem(
                    foodJSON.getString("id"),
                    foodJSON.getString("name"),
                    foodJSON.getString("food_type"),
                    foodJSON.getString("calories"),
                    foodJSON.optDouble("cost")
            ));
        }
        Collections.sort(breakfastMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        JSONArray lunchJSON = diningHall.getJSONArray("lunch_menu");
        ArrayList<FoodItem> lunchMenu = new ArrayList<>();
        for (int j = 0; j < lunchJSON.length(); j++) {
            JSONObject foodJSON = lunchJSON.getJSONObject(j);
            lunchMenu.add(new FoodItem(
                    foodJSON.getString("id"),
                    foodJSON.getString("name"),
                    foodJSON.getString("food_type"),
                    foodJSON.getString("calories"),
                    foodJSON.optDouble("cost")
            ));
        }
        Collections.sort(lunchMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        JSONArray dinnerJSON = diningHall.getJSONArray("dinner_menu");
        ArrayList<FoodItem> dinnerMenu = new ArrayList<>();
        for (int j = 0; j < dinnerJSON.length(); j++) {
            JSONObject foodJSON = dinnerJSON.getJSONObject(j);
            dinnerMenu.add(new FoodItem(
                    foodJSON.getString("id"),
                    foodJSON.getString("name"),
                    foodJSON.getString("food_type"),
                    foodJSON.getString("calories"),
                    foodJSON.optDouble("cost")
            ));
        }
        Collections.sort(dinnerMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));

                        /*
                          Late night is treated specially. Because dining halls without Late Night
                          have its menu as "null" rather than an empty list, we first ask if the
                          dining hall has late night, and then apply the appropriate actions.
                         */
        ArrayList<FoodItem> lateNightMenu = new ArrayList<>();
        if (Arrays.asList(HAS_LATE_NIGHT).contains(name)) {
            JSONArray lateNightJSON = diningHall.getJSONArray("late_night_menu");
            for (int j = 0; j < lateNightJSON.length(); j++) {
                JSONObject foodJSON = lateNightJSON.getJSONObject(j);
                lateNightMenu.add(new FoodItem(
                        foodJSON.getString("id"),
                        foodJSON.getString("name"),
                        foodJSON.getString("food_type"),
                        foodJSON.getString("calories"),
                        foodJSON.optDouble("cost")
                ));
            }
        }
        Collections.sort(lateNightMenu, CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
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
        openingString = diningHall.getString("late_night_open");
        closingString = diningHall.getString("late_night_close");
        Date lateNightOpening = null;
        Date lateNightClosing = null;
        if (!openingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(openingString).getTime();
            lateNightOpening = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        if (!closingString.equals("null")) {
            tmpDate = DATE_FORMAT.parse(closingString).getTime();
            lateNightClosing = new Date(tmpDate + PST.getOffset(tmpDate));
        }
        String imageUrl = diningHall.getString("image_link");
        return new DiningHall(
                id, name, breakfastMenu, lunchMenu, dinnerMenu, lateNightMenu,
                breakfastOpening, breakfastClosing, lunchOpening, lunchClosing,
                dinnerOpening, dinnerClosing, lateNightOpening, lateNightClosing,
                imageUrl
        );
    }
}
