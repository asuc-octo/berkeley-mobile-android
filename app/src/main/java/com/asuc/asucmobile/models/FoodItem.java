package com.asuc.asucmobile.models;

public class FoodItem {

    private String id;
    private String name;
    private String foodType;

    public FoodItem(String id, String name, String foodType) {
        this.id = id;
        this.name = name;
        this.foodType = foodType;
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

}
