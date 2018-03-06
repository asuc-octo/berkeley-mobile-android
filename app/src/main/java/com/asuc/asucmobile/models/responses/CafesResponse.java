package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.Cafe;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodPlace;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rustie on 2/14/18.
 */

public class CafesResponse {

    @SerializedName("cafes")
    private ArrayList<Cafe> cafes;

    public ArrayList<? extends FoodPlace> getCafes() {
        return cafes;
    }

}
