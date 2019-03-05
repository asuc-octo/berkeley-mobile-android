package com.asuc.asucmobile.models;

public class CalendarItem {
    private String date;
    private String info;

    public CalendarItem(String deadline, String information) {
        date = deadline;
        info = information;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }
}
