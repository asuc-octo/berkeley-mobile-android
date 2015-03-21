package com.asuc.asucmobile.models;

import java.util.ArrayList;

public class Line {

    private int id;
    private String name;
    private ArrayList<Stop> stops;

    public Line(int id, String name, ArrayList<Stop> stops) {
        this.id = id;
        this.name = name;
        this.stops = stops;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

}
