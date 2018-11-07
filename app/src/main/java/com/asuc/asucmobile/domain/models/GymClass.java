package com.asuc.asucmobile.domain.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.util.Date;

public class GymClass implements Comparable{
    public static final int ALL_AROUND = 1;
    public static final int CARDIO = 2;
    public static final int MIND = 3;
    public static final int CORE = 4;
    public static final int DANCE = 5;
    public static final int STRENGTH = 6;
    public static final int AQUA = 7;

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

    public int getClassType() {
        switch (classType) {
            case "ALL-AROUND WORKOUT": return ALL_AROUND;
            case "CARDIO": return CARDIO;
            case "MIND/BODY": return MIND;
            case "CORE": return CORE;
            case "DANCE": return DANCE;
            case "STRENGTH": return STRENGTH;
            case "AQUA": return AQUA;
        }
        return 0;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        GymClass b = (GymClass) o;
        return (int) (start.getTime() - b.getStart().getTime());
    }
}
