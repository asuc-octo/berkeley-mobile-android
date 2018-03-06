package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodPlace;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rustie on 2/14/18.
 */

public class DiningHallsResponse {

    @SerializedName("dining_halls")
    private ArrayList<DiningHall> diningHalls;

    public ArrayList<? extends FoodPlace> getDiningHalls() {
        return diningHalls;
    }

}
