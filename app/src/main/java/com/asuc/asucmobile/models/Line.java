package com.asuc.asucmobile.models;

import java.util.ArrayList;

public class Line {

    private int id;
    private ArrayList<Stop> stops;

    public Line(int id, ArrayList<Stop> stops) {
        this.id = id;
        this.stops = stops;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

}
