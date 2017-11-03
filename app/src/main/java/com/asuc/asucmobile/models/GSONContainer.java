package com.asuc.asucmobile.models;

import java.util.ArrayList;

/**
 * Created by alexthomas on 10/10/17.
 */

public class GSONContainer {
    private String id;
    private ArrayList<Trip> trip_list;
    private String created_at;
    private String updated_at;



    public GSONContainer(String id, ArrayList<Trip> trip_list, String created_at, String updated_at) {
        this.id = id;
        this.trip_list = trip_list;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public ArrayList<Trip> getTrip_list() {
        return trip_list;
    }
}
