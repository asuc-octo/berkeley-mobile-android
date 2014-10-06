package com.asuc.asucmobile.models;

import java.util.ArrayList;

public class DiningHall {

    private String id;
    private String name;
    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchMenu;
    private ArrayList<FoodItem> dinnerMenu;

    public DiningHall(String id, String name, ArrayList<FoodItem> breakfastMenu, ArrayList<FoodItem> lunchMenu, ArrayList<FoodItem> dinnerMenu) {
        this.id = id;
        this.name = name;
        this.breakfastMenu = breakfastMenu;
        this.lunchMenu = lunchMenu;
        this.dinnerMenu = dinnerMenu;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<FoodItem> getBreakfastMenu() {
        return breakfastMenu;
    }

    public ArrayList<FoodItem> getLunchMenu() {
        return lunchMenu;
    }

    public ArrayList<FoodItem> getDinnerMenu() {
        return dinnerMenu;
    }

}
