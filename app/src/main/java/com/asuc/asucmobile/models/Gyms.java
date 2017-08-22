package com.asuc.asucmobile.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Gyms {

    @SerializedName("gyms")
    public List<Gym> data;

    @SerializedName("gym")
    public Gym datum;

    public static class Gym implements Cardable {

        public static final double INVALID_COORD = -181;

        private int id;
        private String name;
        private String address;

        @SerializedName("opening_time_today")
        private Date opening;

        @SerializedName("closing_time_today")
        private Date closing;
        private String imageLink;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        private LatLng latLng;
        private boolean hasCoordinates;

        @Override
        public String toString() {
            return "Gym{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", opening=" + opening +
                    ", closing=" + closing +
                    ", imageLink='" + imageLink + '\'' +
                    '}';
        }

        public Gym(int id, String name, String address, Date opening, Date closing, String imageLink) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.opening = opening;
            this.closing = closing;
            this.imageLink = imageLink;
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

        public String getImageLink() {
            return imageLink;
        }

        public void generateLatLng() {
            if (this.latitude == INVALID_COORD || this.longitude == INVALID_COORD) {
                hasCoordinates = false;
            } else {
                hasCoordinates = true;
                this.latLng = new LatLng(this.latitude, this.longitude);
            }
        }

        public LatLng getCoordinates() {
            return hasCoordinates ? latLng : null;
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

        @Override
        public String getTimes() {
            return "Today: " + HOURS_FORMAT.format(this.getOpening()) + "- " +
                    HOURS_FORMAT.format(this.getOpening());
        }
    }
}
