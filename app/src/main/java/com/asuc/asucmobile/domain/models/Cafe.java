package com.asuc.asucmobile.domain.models;

import com.asuc.asucmobile.domain.models.responses.CafeMenuResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by rustie on 9/28/17.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cafe extends FoodPlace {

    // because JSON response for Cafes has a deeper structure...
    @SerializedName("menus")
    public ArrayList<CafeMenuResponse> cafeMenus;
    private CafeMenuResponse breakfast;
    private CafeMenuResponse lunchDinner;

    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchDinnerMenu;
    private Date breakfastOpen;
    private Date lunchDinnerOpen;
    private Date breakfastClose;
    private Date lunchDinnerClose;

    @Builder
    public Cafe(String id, String name, String imageUrl, boolean isOpen, CafeMenuResponse breakfast, CafeMenuResponse lunchDinner) {
        super(id, name, imageUrl, isOpen);
        this.breakfast = breakfast;
        this.lunchDinner = lunchDinner;
    }

    public void setMeals() {
        setBreakfast();
        setLunchDinner();
    }

    private void setBreakfast(){
        if (breakfast == null) {
            for (CafeMenuResponse menu : cafeMenus) {
                if (menu.getMealType().toLowerCase().equals("breakfast")) {
                    breakfast = menu;
                    breakfastOpen = menu.getStart();
                    breakfastClose = menu.getEnd();
                    breakfastMenu = menu.getMenuItems();
                }
            }
        }
    }

    private void setLunchDinner() {
        if (lunchDinner == null) {
            for (CafeMenuResponse menu : cafeMenus) {
                if (menu.getMealType().toLowerCase().equals("lunch\u0026dinner")) {
                    lunchDinner = menu;
                    lunchDinnerOpen = menu.getStart();
                    lunchDinnerClose = menu.getEnd();
                    lunchDinnerMenu = menu.getMenuItems();
                }
            }
        }
    }

    public Date getBreakfastOpening() {
        return breakfastOpen;
    }


    public Date getLunchDinnerOpening() {
        setLunchDinner();
        return lunchDinnerOpen;
    }


    public Date getBreakfastClosing() {
        setBreakfast();
        return breakfastClose;
    }


    public Date getLunchDinnerClosing() {
        setLunchDinner();
        return lunchDinnerClose;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FoodItem> getBreakfastMenu() {
        setBreakfast();
        return breakfastMenu;
    }

    public ArrayList<FoodItem> getLunchDinnerMenu() {
        setLunchDinner();
        return lunchDinnerMenu;
    }




    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * isOpen() returns whether or not this cafe is open
     *
     * @return Boolean indicating if a certain cafe is open or not.
     */
    public boolean isOpen() {
        setBreakfast();
        setLunchDinner();
        if (breakfastOpen == null || lunchDinnerOpen == null
                || breakfastClose == null || lunchDinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(breakfastOpen) && currentTime.before(breakfastClose)
                || currentTime.after(lunchDinnerOpen) && currentTime.before(lunchDinnerClose);
    }

    public boolean isBreakfastOpen() {
        setBreakfast();
        if (breakfastOpen == null || breakfastClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(breakfastOpen) && currentTime.before(breakfastClose);
    }

    public boolean isLunchDinnerOpen() {
        setLunchDinner();
        if (lunchDinnerOpen == null || lunchDinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(lunchDinnerOpen) && currentTime.before(lunchDinnerClose);
    }

}
