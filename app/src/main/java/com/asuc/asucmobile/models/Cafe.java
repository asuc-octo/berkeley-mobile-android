package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rustie on 9/28/17.
 */

public class Cafe extends FoodPlace {



    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchDinnerMenu;
    private Date breakfastOpen;
    private Date lunchDinnerOpen;
    private Date breakfastClose;
    private Date lunchDinnerClose;


    public Cafe(String id, String name, ArrayList<FoodItem> breakfastMenu,
                ArrayList<FoodItem> lunchDinnerMenu, Date breakfastOpen,
                Date lunchDinnerOpen, Date breakfastClose, Date lunchDinnerClose,
                String imageUrl) {
        this.id = id;
        this.name = name;
        this.breakfastMenu = breakfastMenu;
        this.lunchDinnerMenu = lunchDinnerMenu;
        this.breakfastOpen = breakfastOpen;
        this.lunchDinnerOpen = lunchDinnerOpen;
        this.breakfastClose = breakfastClose;
        this.lunchDinnerClose = lunchDinnerClose;
        this.imageUrl = imageUrl;
    }

    public Date getBreakfastOpening() {
        return breakfastOpen;
    }

    public void setBreakfastOpening(Date breakfastOpen) {
        this.breakfastOpen = breakfastOpen;
    }

    public Date getLunchDinnerOpening() {
        return lunchDinnerOpen;
    }

    public void setLunchDinnerOpening(Date lunchDinnerOpen) {
        this.lunchDinnerOpen = lunchDinnerOpen;
    }

    public Date getBreakfastClosing() {
        return breakfastClose;
    }

    public void setBreakfastClosing(Date breakfastClose) {
        this.breakfastClose = breakfastClose;
    }

    public Date getLunchDinnerClosing() {
        return lunchDinnerClose;
    }

    public void setLunchDinnerClosing(Date lunchDinnerClose) {
        this.lunchDinnerClose = lunchDinnerClose;
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
        return breakfastMenu;
    }

    public void setBreakfastMenu(ArrayList<FoodItem> breakfastMenu) {
        this.breakfastMenu = breakfastMenu;
    }

    public ArrayList<FoodItem> getLunchDinnerMenu() {
        return lunchDinnerMenu;
    }

    public void setLunchDinnerMenu(ArrayList<FoodItem> lunchDinnerMenu) {
        this.lunchDinnerMenu = lunchDinnerMenu;
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
        if (breakfastOpen == null || lunchDinnerOpen == null
                || breakfastClose == null || lunchDinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(breakfastOpen) && currentTime.before(breakfastClose)
                || currentTime.after(lunchDinnerOpen) && currentTime.before(lunchDinnerClose);
    }

    public boolean isBreakfastOpen() {
        if (breakfastOpen == null || breakfastClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(breakfastOpen) && currentTime.before(breakfastClose);
    }

    public boolean isLunchDinnerOpen() {
        if (lunchDinnerOpen == null || lunchDinnerClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(lunchDinnerOpen) && currentTime.before(lunchDinnerClose);
    }

}
