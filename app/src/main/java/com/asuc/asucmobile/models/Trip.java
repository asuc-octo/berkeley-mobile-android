package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

public class Trip {

    private Date startTime;
    private Date endTime;
    private ArrayList<Stop> stops;
    private String lineName;

    public Trip (Date startTime, Date endTime, ArrayList<Stop> stops, String lineName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.stops = stops;
        this.lineName = lineName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public String getLineName() {
        return lineName;
    }

}
