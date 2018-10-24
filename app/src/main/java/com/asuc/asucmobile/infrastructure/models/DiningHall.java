package com.asuc.asucmobile.infrastructure.models;

import com.asuc.asucmobile.models.FoodItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Infrastructure model for DiningHalls
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningHall {

    protected String id;
    protected String name;

    @SerializedName("image_link")
    protected String imageUrl;

    // TODO: do something with this
    private boolean isOpen;

    @SerializedName("breakfast_menu")
    private ArrayList<FoodItem> breakfastMenu;

    @SerializedName("lunch_menu")
    private ArrayList<FoodItem> lunchMenu;

    @SerializedName("dinner_menu")
    private ArrayList<FoodItem> dinnerMenu;

    @SerializedName("limited_lunch_menu")
    private ArrayList<FoodItem> limitedLunchMenu;

    @SerializedName("limited_dinner_menu")
    private ArrayList<FoodItem> limitedDinnerMenu;

    @SerializedName("breakfast_open")
    private Date breakfastOpen;

    @SerializedName("breakfast_close")
    private Date breakfastClose;

    @SerializedName("lunch_open")
    private Date lunchOpen;

    @SerializedName("lunch_close")
    private Date lunchClose;

    @SerializedName("dinner_open")
    private Date dinnerOpen;

    @SerializedName("dinner_close")
    private Date dinnerClose;

    @SerializedName("limited_lunch_open")
    private Date limitedLunchOpen;

    @SerializedName("limited_lunch_close")
    private Date limitedLunchClose;

    @SerializedName("limited_dinner_open")
    private Date limitedDinnerOpen;

    @SerializedName("limited_dinner_close")
    private Date limitedDinnerClose;

}
