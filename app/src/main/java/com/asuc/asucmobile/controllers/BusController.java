package com.asuc.asucmobile.controllers;

import android.content.Context;
import com.asuc.asucmobile.models.Buses;
import com.asuc.asucmobile.models.Buses.Bus;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class BusController {

    public interface cService {

        String PATH = "bt_buses/";

        @GET(PATH)
        Call<Buses> getData();

    }

    public static List<Bus> parse(Buses buses) {
        Set<Bus> toRemove = new HashSet<>();
        for (int i = 0; i < buses.data.size(); i++) {
            if (!buses.data.get(i).isInService()) {
                toRemove.add(buses.data.get(i));
            } else {
                buses.data.get(i).generateLatLng();
            }
        }
        buses.data.removeAll(toRemove);
        return buses.data;
    }

}
