package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.Category;
import com.asuc.asucmobile.domain.models.CategoryLoc;
import com.asuc.asucmobile.domain.models.Resource;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alexthomas on 4/16/18.
 */

public class MapIconResponse {
    @SerializedName("Nap Pod")
    private ArrayList<CategoryLoc> napPods;

    @SerializedName("Water Fountain")
    private ArrayList<CategoryLoc> waterFountains;

    @SerializedName("Microwave")
    private ArrayList<CategoryLoc> microwaves;

    public ArrayList<CategoryLoc> getNapPods() {
        return napPods;
    }

    public ArrayList<CategoryLoc> getWaterFountains() {
        return waterFountains;
    }

    public ArrayList<CategoryLoc> getMicrowaves() {
        return microwaves;
    }
}
