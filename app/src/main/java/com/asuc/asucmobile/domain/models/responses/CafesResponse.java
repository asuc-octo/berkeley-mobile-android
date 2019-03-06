package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.FoodPlace;
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
