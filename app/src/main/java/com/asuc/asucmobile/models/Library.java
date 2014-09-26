package com.asuc.asucmobile.models;

import java.util.Date;

public class Library {

    private String id;
    private String name;
    private String location;
    private String phone;
    private Date opening;
    private Date closing;
    private boolean byAppointment;

    public Library(String id, String name, String location, String phone, Date opening, Date closing, boolean byAppointment) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.opening = opening;
        this.closing = closing;
        this.byAppointment = byAppointment;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public Date getOpening() {
        return opening;
    }

    public Date getClosing() {
        return closing;
    }

    public boolean isByAppointment() {
        return byAppointment;
    }

}
