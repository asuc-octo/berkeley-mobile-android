package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Resource implements Comparable<Resource>{

    String resource;
    String topic;
    String phone1;
    String phone2;
    String email;
    String location;
    String hours;
    String onOrOffCampus;
    double lat;
    double lng;
    String notes;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getOnOrOffCampus() {
        return onOrOffCampus;
    }

    public void setOnOrOffCampus(String onOrOffCampus) {
        this.onOrOffCampus = onOrOffCampus;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Resource(String resource, String topic, String phone1, String phone2, String email,
                    String location, String hours, String onOrOffCampus, double lat, double lng,
                    String notes) {
        this.resource = resource;
        this.topic = topic;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.location = location;
        this.hours = hours;
        this.onOrOffCampus = onOrOffCampus;
        this.lat = lat;
        this.lng = lng;
        this.notes = notes;
    }

    @Override
    public int compareTo(Resource other) {
        return this.getResource().compareTo(other.getResource());
    }

}
