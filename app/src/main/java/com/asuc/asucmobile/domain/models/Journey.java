package com.asuc.asucmobile.domain.models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexthomas on 10/11/17.
 */

public class Journey implements java.io.Serializable {
    private String name;
    private ArrayList<Trip> trips;

    public Journey(ArrayList<Trip> trips) {
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
        ArrayList<Stop> stops = new ArrayList<>();
        Date previousArrival = null;
        for (Trip trip : trips) {
            stops.add(new Stop(trip.getLineName(), trip.getDepartureTime(), previousArrival));
            previousArrival = trip.getArrivalTime();
            for (Stop stop : trip.getStops()) {
                stops.add(stop);
            }
        }
        if (stops.size() == 2) { // One trip and one stop list. Need to add extra stop.
            stops.add(trips.get(0).getStops().get(0));
        }
        Stop lastStop = new Stop("", null, previousArrival);
        lastStop.setID(1);
        stops.add(lastStop);
        return stops;
    }

}
