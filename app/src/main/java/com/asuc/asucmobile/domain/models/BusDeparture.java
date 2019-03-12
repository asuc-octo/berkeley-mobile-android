package com.asuc.asucmobile.domain.models;

/**
 * Created by alexthomas on 10/9/17.
 */

public class BusDeparture {
    private long epochTime;
    private int minutes;
    private int seconds;
    private Direction direction;
    private Vehicle vehicle;
    private boolean isDeparture;
    private String busRouteName;

    public BusDeparture(Direction direction, long epochTime, int minutes, int seconds, Vehicle vehicle, boolean isDeparture) {
        this.epochTime = epochTime;
        this.minutes = minutes;
        this.seconds = seconds;
        this.vehicle = vehicle;
        this.isDeparture = isDeparture;
        this.busRouteName = direction.getTitle();
    }

    public long getEpochTime() {
        return epochTime;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getBusRouteName() {
        return this.direction.getTitle();
    }
}
