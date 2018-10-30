package com.asuc.asucmobile.infrastructure.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Library {

    private int id;
    private String name;
    @SerializedName("campus_location")
    private String location;
    @SerializedName("phone_number")
    private String phone;
    @SerializedName("opening_time_today")
    private Date opening;
    @SerializedName("closing_time_today")
    private Date closing;
    @SerializedName("weekly_opening_times")
    private ArrayList<Date> weeklyOpen;
    @SerializedName("weekly_closing_times")
    private ArrayList<Date> weeklyClose;
    private LatLng latLng;
    private double latitude;
    private double longitude;
    private boolean byAppointment;
    private boolean hasCoordinates;

    @SerializedName("weekly_by_appointment")
    private ArrayList<Boolean> weeklyAppointments;
    private int weekday;
}
