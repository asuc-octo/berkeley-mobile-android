package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;

public class FoodItem implements Comparable<FoodItem> {

    private String id;
    private String name;
    private String foodType;
    private String calories;
    private double cost;

    public FoodItem(String id, String name, String calories, double cost, String foodType) {
        this.id = id;
        this.name = name;
        this.foodType = foodType;
        this.calories = calories;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFoodTypes() {
        return foodType;
    }

    public String getCalories() {
        return calories;
    }

    public String getCost() {
        DecimalFormat df = new DecimalFormat("0.00");
        return "$" + df.format(cost);
    }

    @Override
    public int compareTo(@NonNull FoodItem other) {
        return this.getName().compareTo(other.getName());
    }

}
