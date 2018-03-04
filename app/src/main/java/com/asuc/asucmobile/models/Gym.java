package com.asuc.asucmobile.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gym implements Cardable {

    private int id;
    private String name;
    private String address;
    private Date opening;
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

    @Override
    public String getImageLink() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTimes() {
        return parseTimes();
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

    private String parseTimes(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(opening) + " - " + df.format(closing);
    }

}
