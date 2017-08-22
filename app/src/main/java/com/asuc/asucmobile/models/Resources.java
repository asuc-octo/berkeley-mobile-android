package com.asuc.asucmobile.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resources {

    @SerializedName("resources")
    public List<Resource> data;

    public class Resource implements Comparable<Resource> {

        public static final double INVALID_COORD = -181;

        private boolean hasCoordinates;

        @SerializedName("Resource")
        private String resource;

        @SerializedName("Topic")
        private String topic;

        @SerializedName("Phone 1")
        private String phone1;

        @SerializedName("Phone 2 (Optional)")
        private String phone2;

        @SerializedName("Office Location")
        private String location;

        @SerializedName("Hours")
        private String hours;

        @SerializedName("Email")
        private String email;

        @SerializedName("On/Off Campus")
        private String onOrOffCampus;

        @SerializedName("Notes")
        private String notes;
        private LatLng latLng;

        @SerializedName("Latitude")
        private double lat;

        @SerializedName("Longitude")
        private double lng;

        public Resource(String resource, String topic, String phone1, String phone2, String location,
                        String hours, String email, String onOrOffCampus, double lat, double lng,
                        String notes) {
            this.resource = resource;
            this.topic = topic;
            this.phone1 = phone1;
            this.phone2 = phone2;
            this.location = location;
            this.hours = hours;
            this.email = email;
            this.onOrOffCampus = onOrOffCampus;
            this.notes = notes;
            this.lat = lat;
            this.lng = lng;
            if (lat == INVALID_COORD || lng == INVALID_COORD) {
                hasCoordinates = false;
            } else {
                hasCoordinates = true;
                this.latLng = new LatLng(lat, lng);
            }
        }

        public String getResource() {
            return resource;
        }

        public String getTopic() {
            return topic;
        }

        public String getPhone1() {
            return phone1;
        }

        public String getPhone2() {
            return phone2;
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

        public String getEmail() {
            return email;
        }

        public String getOnOrOffCampus() {
            return onOrOffCampus;
        }

        public LatLng getCoordinates() {
            return hasCoordinates ? latLng : null;
        }

        public String getNotes() {
            return notes;
        }

        @Override
        public int compareTo(@NonNull Resource other) {
            return this.getResource().compareTo(other.getResource());
        }

        public void generateLatLng() {
            if (this.lat == INVALID_COORD || this.lng == INVALID_COORD) {
                hasCoordinates = false;
            } else {
                hasCoordinates = true;
                this.latLng = new LatLng(this.lat, this.lng);
            }
        }
    }
}
