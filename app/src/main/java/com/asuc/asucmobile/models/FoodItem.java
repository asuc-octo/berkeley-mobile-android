package com.asuc.asucmobile.models;

public class FoodItem {

    private String id;
    private String name;
    private String foodType;
    private String calories;

    public FoodItem(String id, String name, String foodType, String calories) {
        this.id = id;
        this.name = name;
        this.foodType = foodType;
        this.calories = calories;
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

}
