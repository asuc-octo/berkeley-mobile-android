package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.models.Gyms;
import com.asuc.asucmobile.models.Gyms.Gym;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class GymController {

    private static Gym currentGym;

    public interface cService {
        @GET("gyms")
        Call<Gyms> getGyms();
    }
    public static List<Gym> parse(Gyms gyms) {
        return gyms.gyms;
    }

    public static void setCurrentGym(Gym gym) {
        currentGym = gym;
    }

    public static Gym getCurrentGym() {
        return currentGym;
    }

}
