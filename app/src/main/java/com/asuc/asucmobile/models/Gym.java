package com.asuc.asucmobile.models;

import java.util.Date;

public class Gym {

    private int id;
    private String name;
    private String address;
    private Date opening;
    private Date closing;
    private String imageUrl;
    private int capacity;
    private int count;

    public Gym(int id, String name, String address, Date opening, Date closing, String imageUrl,
               Integer capacity, Integer count) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.opening = opening;
        this.closing = closing;
        this.imageUrl = imageUrl;
        this.capacity = capacity;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Date getOpening() {
        return opening;
    }

    public Date getClosing() {
        return closing;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCount() {
        return count;
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the gym is open or not.
     */
    public boolean isOpen() {
        if (opening == null || closing == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(opening) && currentTime.before(closing);
    }

}
