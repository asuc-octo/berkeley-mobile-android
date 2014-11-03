package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

public class DiningHall {

    private String id;
    private String name;
    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchMenu;
    private ArrayList<FoodItem> dinnerMenu;
    private Date breakfastOpen;
    private Date breakfastClose;
    private Date lunchOpen;
    private Date lunchClose;
    private Date dinnerOpen;
    private Date dinnerClose;
    private Date lateOpen;
    private Date lateClose;
    private String imageUrl;

    public DiningHall(String id, String name, ArrayList<FoodItem> breakfastMenu,
                      ArrayList<FoodItem> lunchMenu, ArrayList<FoodItem> dinnerMenu,
                      Date breakfastOpen, Date breakfastClose, Date lunchOpen, Date lunchClose,
                      Date dinnerOpen, Date dinnerClose, Date lateOpen, Date lateClose,
                      String imageUrl) {
        this.id = id;
        this.name = name;
        this.breakfastMenu = breakfastMenu;
        this.lunchMenu = lunchMenu;
        this.dinnerMenu = dinnerMenu;
        this.breakfastOpen = breakfastOpen;
        this.breakfastClose = breakfastClose;
        this.lunchOpen = lunchOpen;
        this.lunchClose = lunchClose;
        this.dinnerOpen = dinnerOpen;
        this.dinnerClose = dinnerClose;
        this.lateOpen = lateOpen;
        this.lateClose = lateClose;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
    
    public Date getBreakfastOpening() {
        return breakfastOpen;
    }
    
    public Date getBreakfastClosing() {
        return breakfastClose;
    }

    public Date getLunchOpening() {
        return lunchOpen;
    }

    public Date getLunchClosing() {
        return lunchClose;
    }

    public Date getDinnerOpening() {
        return dinnerOpen;
    }

    public Date getDinnerClosing() {
        return dinnerClose;
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

    public boolean isLateNightOpen() {
        if (lateOpen == null || lateClose == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(lateOpen) && currentTime.before(lateClose);
    }

}
