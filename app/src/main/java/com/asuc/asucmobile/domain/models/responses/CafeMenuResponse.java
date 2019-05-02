package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.FoodItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rustie on 2/23/18.
 */

public class CafeMenuResponse {

    public int id;

    @SerializedName("start_time")
    public Date start;

    @SerializedName("end_time")
    public Date end;

    @SerializedName("meal_type")
    public String mealType;

    @SerializedName("menu_items")
    public ArrayList<FoodItem> menuItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public ArrayList<FoodItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<FoodItem> menuItems) {
        this.menuItems = menuItems;
    }
}
