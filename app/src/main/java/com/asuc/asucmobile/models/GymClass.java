package com.asuc.asucmobile.models;

import java.sql.Time;
import java.util.Date;

/**
 * Created by sudarshan on 3/1/18.
 */

public class GymClass {
    public static final int ALL_AROUND = 1;
    public static final int CARDIO = 2;
    public static final int MIND = 3;
    public static final int CORE = 4;
    public static final int DANCE = 5;
    public static final int STRENGTH = 6;
    public static final int AQUA = 7;


    private int id;
    private Date date;
    private Date startTime;
    private Date endTime;
    private String name;
    private String description;
    private String trainer;
    private int classType;
    private String location;


    public GymClass(int id, Date date, Date startTime, Date endTime, String name, String description, String trainer, int classType, String location) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.description = description;
        this.trainer = trainer;
        this.classType = classType;
        this.location = location;
    }

    public static int getAllAround() {
        return ALL_AROUND;
    }

    public static int getCARDIO() {
        return CARDIO;
    }

    public static int getMIND() {
        return MIND;
    }

    public static int getCORE() {
        return CORE;
    }

    public static int getDANCE() {
        return DANCE;
    }

    public static int getSTRENGTH() {
        return STRENGTH;
    }

    public static int getAQUA() {
        return AQUA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
