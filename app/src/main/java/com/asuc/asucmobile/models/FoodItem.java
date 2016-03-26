package com.asuc.asucmobile.models;

import java.text.DecimalFormat;

public class FoodItem implements Comparable<FoodItem> {

    private String id;
    private String name;
    private String foodType;
    private String calories;
    private double cost;

    public FoodItem(String id, String name, String foodType, String calories, double cost) {
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

    public String getFoodType() {
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
    public int compareTo(FoodItem other) {
        return this.getName().compareTo(other.getName());
    }

}
