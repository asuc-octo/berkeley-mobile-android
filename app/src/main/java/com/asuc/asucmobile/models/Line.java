package com.asuc.asucmobile.models;

import java.util.ArrayList;

/**
 * Created by alexthomas on 10/10/17.
 */

public class Line implements java.io.Serializable {

    private int id;
    private String name;
    private ArrayList<Stop> stop_list;

    public Line(int id, String name, ArrayList<Stop> stop_list) {
        this.id = id;
        this.name = name;
        this.stop_list = stop_list;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Stop> getStops() {
        return stop_list;
    }

}
