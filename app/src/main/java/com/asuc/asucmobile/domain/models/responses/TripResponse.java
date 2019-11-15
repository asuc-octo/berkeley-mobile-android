package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.TripRespModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripResponse {

    @SerializedName("journeys")
    private ArrayList<TripRespModel> tripRespModels;

    public ArrayList<TripRespModel> getTripRespModels() {
        return tripRespModels;
    }
}
