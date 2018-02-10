package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.models.DiningHall;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by rustie on 2/10/18.
 */

public interface BMAPI {

    @Headers("Cache-Control: max-age=640000")
    @GET("dining_halls")
    Call<List<DiningHall>> diningHallList();

}
