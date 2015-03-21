package com.asuc.asucmobile.models;

import java.util.ArrayList;
import java.util.Date;

public class Route {

    private String name;
    private ArrayList<Trip> trips;

    public Route (ArrayList<Trip> trips) {
        name = "";
        for (Trip trip : trips) {
            name += trip.getLineName() + "\n\n";
        }
        name = name.substring(0, name.length() - 2);

        this.trips = trips;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public ArrayList<Stop> getStopsAndTransfers() {
        ArrayList<Stop> stops = new ArrayList<Stop>();

        Date previousArrival = null;
        for (Trip trip : trips) {
            stops.add(new Stop(trip.getLineName(), trip.getStartTime(), previousArrival));
            previousArrival = trip.getEndTime();

            for (Stop stop : trip.getStops()) {
                stops.add(stop);
            }
        }

        stops.add(new Stop("", null, previousArrival));

        return stops;
    }

}
