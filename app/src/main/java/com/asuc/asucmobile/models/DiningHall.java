package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

public class DiningHall {

    private String id;
    private String name;
    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchMenu;
    private ArrayList<FoodItem> dinnerMenu;
    private ArrayList<FoodItem> limitedLunchMenu;
    private ArrayList<FoodItem> limitedDinnerMenu;

    private Date breakfastOpen;
    private Date breakfastClose;
    private Date lunchOpen;
    private Date lunchClose;
    private Date dinnerOpen;
    private Date dinnerClose;
    private Date limitedLunchOpen;
    private Date limitedLunchClose;
    private Date limitedDinnerOpen;
    private Date limitedDinnerClose;
    private String imageUrl;

    public DiningHall(String id, String name, ArrayList<FoodItem> breakfastMenu,
                      ArrayList<FoodItem> lunchMenu, ArrayList<FoodItem> dinnerMenu,
                      ArrayList<FoodItem> limitedLunchMenu, ArrayList<FoodItem> limitedDinnerMenu, Date breakfastOpen, Date breakfastClose,
                      Date lunchOpen, Date lunchClose, Date dinnerOpen, Date dinnerClose,
                      Date limitedLunchOpen, Date limitedLunchClose, Date limitedDinnerOpen, Date limitedDinnerClose, String imageUrl) {
        this.id = id;
        this.name = name;
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

    public ArrayList<FoodItem> getLimitedLunchMenu() {
        return limitedLunchMenu;
    }

    public ArrayList<FoodItem> getLimitedDinnerMenu() {
        return limitedDinnerMenu;
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

    public Date getLimitedLunchOpen() {
        return limitedLunchOpen;
    }

    public Date getLimitedLunchClosing() {
        return limitedLunchClose;
    }

    public Date getLimitedDinnerOpen() {
        return limitedDinnerOpen;
    }

    public Date getLimitedDinnerClosing() {
        return limitedDinnerClose;
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

}
