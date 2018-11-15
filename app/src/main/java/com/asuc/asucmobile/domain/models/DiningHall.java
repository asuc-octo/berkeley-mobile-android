package com.asuc.asucmobile.domain.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiningHall extends FoodPlace{


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

    @Builder
    public DiningHall(String id, String name, String imageUrl, boolean isOpen, ArrayList<FoodItem> breakfastMenu, ArrayList<FoodItem> lunchMenu, ArrayList<FoodItem> dinnerMenu, ArrayList<FoodItem> limitedLunchMenu, ArrayList<FoodItem> limitedDinnerMenu, Date breakfastOpen, Date breakfastClose, Date lunchOpen, Date lunchClose, Date dinnerOpen, Date dinnerClose, Date limitedLunchOpen, Date limitedLunchClose, Date limitedDinnerOpen, Date limitedDinnerClose) {
        super(id, name, imageUrl, isOpen);
        this.breakfastMenu = breakfastMenu;
        this.lunchMenu = lunchMenu;
        this.dinnerMenu = dinnerMenu;
        this.limitedLunchMenu = limitedLunchMenu;
        this.limitedDinnerMenu = limitedDinnerMenu;
        this.breakfastOpen = breakfastOpen;
        this.breakfastClose = breakfastClose;
        this.lunchOpen = lunchOpen;
        this.lunchClose = lunchClose;
        this.dinnerOpen = dinnerOpen;
        this.dinnerClose = dinnerClose;
        this.limitedLunchOpen = limitedLunchOpen;
        this.limitedLunchClose = limitedLunchClose;
        this.limitedDinnerOpen = limitedDinnerOpen;
        this.limitedDinnerClose = limitedDinnerClose;
    }

    /**
     * isBreakfastOpen() returns whether or not breakfast is open. The same goes for the other
     * three open methods.
     *
     * @return Boolean indicating if a certain dining hall menu is open or not.
     */
    public boolean isBreakfastOpen() {
        if (breakfastOpen == null || breakfastClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(breakfastOpen) && currentTime.before(breakfastClose);
    }

    public boolean isLunchOpen() {
        if (lunchOpen == null || lunchClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(lunchOpen) && currentTime.before(lunchClose);
    }

    public boolean isDinnerOpen() {
        if (dinnerOpen == null || dinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(dinnerOpen) && currentTime.before(dinnerClose);
    }

    public boolean isLimitedLunchOpen() {
        if (limitedLunchOpen == null || limitedLunchClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(limitedLunchOpen) && currentTime.before(limitedLunchClose);
    }

    public boolean isLimitedDinnerOpen() {
        if (limitedDinnerOpen == null || limitedDinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(limitedDinnerOpen) && currentTime.before(limitedDinnerClose);
    }

    public boolean limitedLunchToday() {
        return limitedLunchOpen != null && limitedLunchClose != null;
    }

    public boolean limitedDinnerToday() {
        return limitedDinnerOpen != null && limitedDinnerClose != null;
    }

    public boolean isOpen() {
        return isBreakfastOpen() | isLunchOpen() | isDinnerOpen() | isLimitedLunchOpen() | isLimitedDinnerOpen();
    }

    @Override
    public String toString() {
        return "DiningHall{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", breakfastMenu=" + breakfastMenu +
                ", lunchMenu=" + lunchMenu +
                ", dinnerMenu=" + dinnerMenu +
                ", limitedLunchMenu=" + limitedLunchMenu +
                ", limitedDinnerMenu=" + limitedDinnerMenu +
                ", breakfastOpen=" + breakfastOpen +
                ", breakfastClose=" + breakfastClose +
                ", lunchOpen=" + lunchOpen +
                ", lunchClose=" + lunchClose +
                ", dinnerOpen=" + dinnerOpen +
                ", dinnerClose=" + dinnerClose +
                ", limitedLunchOpen=" + limitedLunchOpen +
                ", limitedLunchClose=" + limitedLunchClose +
                ", limitedDinnerOpen=" + limitedDinnerOpen +
                ", limitedDinnerClose=" + limitedDinnerClose +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}
