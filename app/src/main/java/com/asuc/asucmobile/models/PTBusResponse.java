package com.asuc.asucmobile.models;

import java.util.Collection;
import java.util.List;

/**
 * Created by alexthomas on 5/26/17.
 */

public class PTBusResponse {
    private int minutes, seconds;
    private Direction direction;
    private Route route;
    private Agency agency;
    private Collection<BusDeparture> values;
    private String routeBusTitle;


    public PTBusResponse(
                         int minutes, int seconds, Route route, Agency agency, Collection<BusDeparture> values) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.route = route;
        this.agency = agency;
        this.values = values;
    }




    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public Direction getDirection() {
        return direction;
    }

    public Collection<BusDeparture> getValues() {
        return values;
    }

    public Route getRoute() {
        return route;
    }

    public String getRouteBusTitle() {
        return routeBusTitle;
    }
}
