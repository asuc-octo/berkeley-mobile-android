package com.asuc.asucmobile.domain.models;

import com.asuc.asucmobile.infrastructure.models.TripBeforeTransform;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripRespModel {

    @SerializedName("id")
    private int id;

    @SerializedName("trip_list")
    private ArrayList<TripBeforeTransform> tripList;

    public ArrayList<TripBeforeTransform> getTripList() {
        return tripList;
    }

    public int getId() {
        return id;
    }

}
