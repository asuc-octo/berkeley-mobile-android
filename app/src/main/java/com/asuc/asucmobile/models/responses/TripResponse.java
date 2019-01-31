package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.TripRespModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripResponse {

    @SerializedName("journeys")
    private ArrayList<TripRespModel> tripRespModels;

    public ArrayList<TripRespModel> getTripRespModels() {
        return tripRespModels;
    }
}
