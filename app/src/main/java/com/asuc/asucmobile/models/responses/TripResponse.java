package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.TripBeforeTransform;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripResponse {

    @SerializedName("trip_list")
    private ArrayList<TripBeforeTransform> tripList;

    public ArrayList<TripBeforeTransform> getTripList() {
        return tripList;
    }

}
