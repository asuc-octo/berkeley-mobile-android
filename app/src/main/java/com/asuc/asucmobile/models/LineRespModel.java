package com.asuc.asucmobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LineRespModel {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("stop_list")
    private ArrayList<StopBeforeTransform> lineStops;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<StopBeforeTransform> getLineStops() {
        return lineStops;
    }
}
