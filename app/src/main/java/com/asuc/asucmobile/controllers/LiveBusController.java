package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.domain.models.Buses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by alexthomas on 11/12/17.
 */

public class LiveBusController {

    public interface cService {

        String PATH = "bt_buses/";

        @GET(PATH)
        Call<Buses> getData();

    }

    public static List<Buses.Bus> parse(Buses buses) {
        Set<Buses.Bus> toRemove = new HashSet<>();
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
