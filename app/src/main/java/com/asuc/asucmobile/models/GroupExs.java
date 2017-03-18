package com.asuc.asucmobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class GroupExs {
    public List<GroupEx> groupExs;

    public class GroupEx {

        Date date;
        Date startTime;
        Date endTime;
        String name;
        String description;
        String trainer;
        String classType;
        String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Date getDate() {
            return date;

        }

        public Date getStartTime() {
            return startTime;
        }

        public Date getEndTime() {
            return endTime;
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

        public String getClassType() {
            return classType;
        }
    }
}


