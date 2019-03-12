package com.asuc.asucmobile.models;

public class CalendarItem {
    private String date;
    private String info;
    private boolean isHeader;

    public CalendarItem(String deadline, String information) {
        date = deadline;
        info = information;
        isHeader = false;
    }

    public CalendarItem(String deadline, String information, boolean header) {
        date = deadline;
        info = information;
        isHeader = true;
    }

    public String getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }

    public boolean getIsHeader() {
        return isHeader;
    }
}
