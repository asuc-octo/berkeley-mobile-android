package com.asuc.asucmobile.domain.models;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gym {

    private int id;
    private String name;
    private String address;
    @SerializedName("opening_times")
    private ArrayList<Date> opening;
    @SerializedName("closing_times")
    private ArrayList<Date> closing;

    @SerializedName("image_link")
    private String imageUrl;

    public Gym(int id, String name, String address, ArrayList<Date> opening, ArrayList<Date> closing, String imageUrl) {
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

    public String getTimes() {
        return parseTimes();
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Date> getOpening() {
        return opening;
    }

    public ArrayList<Date> getClosing() {
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
        boolean isOpen = false;
        if (opening.size() == closing.size()) {
            for (int i = 0; i < opening.size(); i++) {
                isOpen = isOpen || (currentTime.after(opening.get(i)) && currentTime.before(closing.get(i)));
            }
            return isOpen;
        } else {
            return false;
        }
    }

    private String parseTimes(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(opening) + " - " + df.format(closing);
    }

}