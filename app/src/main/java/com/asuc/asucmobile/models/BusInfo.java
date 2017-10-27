package com.asuc.asucmobile.models;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexthomas on 5/28/17.
 */

public class BusInfo {

    private ArrayList<Integer> nearTimes;
    private String nearestTime;
    private String title;
    private String distance;
    private ArrayList<BusDeparture> times;
    private String routeName;
    private int mainTime, mainTime1, mainTime2;
    private String routeBusTitle;


    public BusInfo(String title, ArrayList<BusDeparture> times, String routeName, String routeBusTitle) {
        this.title = title;
        this.times = times;
        this.routeName = routeName.split(" ")[0];
        this.nearTimes = getNearestTimes(times);
        this.routeBusTitle = routeBusTitle;
    }


    private String convertEpochTime(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String time_stamp = sdf.format(new Date(time));
        return time_stamp;
    }

    //Gets 10 nearest times
    private ArrayList<Integer> getNearestTimes(ArrayList<BusDeparture> times) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < times.size(); i += 1) {
            if (i >= 3) {
                break;
            } else
                list.add(times.get(i).getMinutes());
        }

        return list;
    }

    public String getNearestTime() {
        return nearestTime;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Integer> getNearTimes() {
        return nearTimes;
    }

    public ArrayList<BusDeparture> getTimes() {
        return times;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteBusTitle() {
        return routeBusTitle;
    }
}
