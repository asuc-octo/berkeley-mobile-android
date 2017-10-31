package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.Set;

public class FoodItem implements Comparable<FoodItem> {

    private String id;
    private String name;
    private String calories;
    private double cost;

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
        DecimalFormat df = new DecimalFormat("0.00");
        return "$" + df.format(cost);
    }

    public ArrayList<String> getFoodTypes() {
        return foodTypes;
    }

    @Override
    public int compareTo(@NonNull FoodItem other) {
        return this.getName().compareTo(other.getName());
    }

}