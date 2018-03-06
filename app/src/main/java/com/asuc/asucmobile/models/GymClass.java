package com.asuc.asucmobile.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.util.Date;

public class GymClass {

    private int id;
    private String date;
    private String name;
    private String description;
    private String trainer;
    private String location;

    @SerializedName("image_link")
    private String imageUrl;

    @SerializedName("class_type")
    private String classType;

    @SerializedName("start_time")
    private Date start;

    @SerializedName("end_time")
    private Date end;

    /**
     * Whether this class is happening currently
     * @return
     */
    public boolean isHappening() {
        if (start == null || end == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(start) && currentTime.before(end);
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTrainer() {
        return trainer;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getClassType() {
        return classType;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
