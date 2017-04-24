package com.asuc.asucmobile.controllers;

import android.content.Context;

import com.asuc.asucmobile.models.DiningHalls;
import com.asuc.asucmobile.models.DiningHalls.DiningHall;
import com.asuc.asucmobile.utilities.CustomComparators;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class DiningController {

    private static DiningHall currentDiningHall;

    public interface cService {

        String PATH = "dining_halls/";

        @GET(PATH)
        Call<DiningHalls> getData();

        @GET(PATH + "{id}/")
        Call<DiningHalls> getDatum(@Path("id") int id);

    }


    public static List<DiningHall> parse(DiningHalls diningHalls, Context context) {
        for (DiningHall diningHall : diningHalls.data) {
            parseDatum(diningHall, context);
        }
        return diningHalls.data;
    }

    public static DiningHall parseDatum(DiningHall diningHall, Context context) {
        Collections.sort(diningHall.getBreakfastMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        Collections.sort(diningHall.getLunchMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        Collections.sort(diningHall.getDinnerMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        if (diningHall.getLateNightMenu() != null) {
            Collections.sort(diningHall.getBreakfastMenu(), CustomComparators.FacilityComparators.getFoodSortByFavorite(context));
        }
        return diningHall;
    }

    public static void setCurrentDiningHall(DiningHall diningHall) {
        currentDiningHall = diningHall;
    }

    public static DiningHall getCurrentDiningHall() {
        return currentDiningHall;
    }

}
