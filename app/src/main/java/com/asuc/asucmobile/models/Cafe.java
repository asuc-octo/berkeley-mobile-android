package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rustie on 9/28/17.
 */

public class Cafe {


    private String id;
    private String name;
    private ArrayList<FoodItem> breakfastMenu;
    private ArrayList<FoodItem> lunchDinnerMenu;
    private Date open;
    private Date close;
    private String imageUrl;

    public Cafe(String id, String name, ArrayList<FoodItem> breakfastMenu,
                ArrayList<FoodItem> lunchDinnerMenu, Date open, Date close, String imageUrl) {
        this.id = id;
        this.name = name;
        this.breakfastMenu = breakfastMenu;
        this.lunchDinnerMenu = lunchDinnerMenu;
        this.open = open;
        this.close = close;
        this.imageUrl = imageUrl;
    }



    /**
     * isOpen() returns whether or not this cafe is open
     *
     * @return Boolean indicating if a certain cafe is open or not.
     */
    public boolean isOpen() {
        if (open == null || close == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(open) && currentTime.before(close);
    }

}
