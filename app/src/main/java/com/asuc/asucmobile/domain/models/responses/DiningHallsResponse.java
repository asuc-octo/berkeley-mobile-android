package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.FoodPlace;
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
