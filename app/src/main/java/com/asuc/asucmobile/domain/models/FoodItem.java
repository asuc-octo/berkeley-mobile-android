package com.asuc.asucmobile.domain.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.text.DecimalFormat;

public class FoodItem implements Comparable<FoodItem> {

    private String id;
    private String name;
    private String calories;
    private double cost;

    private boolean lower = false;

    @SerializedName("food_type")
    private ArrayList<String> foodTypes; // all in upper case

    public FoodItem(String id, String name, String calories, double cost, ArrayList<String> types) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.cost = cost;
        this.foodTypes = types;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCalories() {
        return calories;
    }

    public String getCost() {
        if (Double.isNaN(cost)) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return "$" + df.format(cost);
    }

    /**
     * Upon first call, make all the food types lower case. Since we're fetching food types using
     * Retrofit's POJO direct serialization, we have to do conversion to lower here.
     * @return
     */
    public ArrayList<String> getFoodTypes() {
        if (!lower) {
            lower = true;
            for (int i = 0; i < foodTypes.size(); i++) {
                foodTypes.set(i, foodTypes.get(i).toLowerCase());
            }
        }
        return foodTypes;
    }


    @Override
    public int compareTo(@NonNull FoodItem other) {
        return this.getName().compareTo(other.getName());
    }

}