package com.asuc.asucmobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Gym {

    private int id;
    private String name;
    private String address;
    @SerializedName("opening_time_today")
    private Date opening;
    @SerializedName("closing_time_today")
    private Date closing;
    private String imageUrl;

    public Gym(int id, String name, String address, Date opening, Date closing, String imageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.opening = opening;
        this.closing = closing;
        this.imageUrl = imageUrl;
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
