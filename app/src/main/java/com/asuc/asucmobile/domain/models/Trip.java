package com.asuc.asucmobile.domain.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexthomas on 10/10/17.
 */

public class Trip implements java.io.Serializable {
    private String id;
    private Stop starting_stop;
    private Stop destination_stop;
    private String line_name;
    private String arrival_time;
    private String departure_time;
    private int line_id;
    private ArrayList<Stop> stops;
    private Date arrivalTime;
    private Date departureTime;

    public Trip(String id, Stop starting_stop, Stop destination_stop, String line_name, String arrival_time, String departure_time, int line_id) {
        this.id = id;
        this.starting_stop = starting_stop;
        this.destination_stop = destination_stop;
        this.line_name = line_name;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.line_id = line_id;
    }

    public Trip(Date departure_time, Date end_Time, ArrayList<Stop> stops, String line_name) {
        this.stops = stops;
        this.departureTime = departure_time;
        this.arrivalTime = end_Time;
        this.line_name = line_name;

    }

    public String getLineName() {
        return line_name;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public Stop getStarting_stop() {
        return starting_stop;
    }

    public Stop getDestination_stop() {
        return destination_stop;
    }

    public int getLine_id() {
        return line_id;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }
}
