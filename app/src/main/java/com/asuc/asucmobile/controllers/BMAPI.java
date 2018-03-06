package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.models.responses.CafesResponse;
import com.asuc.asucmobile.models.responses.DiningHallsResponse;
import com.asuc.asucmobile.models.responses.GymClassesResponse;
import com.asuc.asucmobile.models.responses.GymsResponse;
import com.asuc.asucmobile.models.responses.LibrariesResponse;
import com.asuc.asucmobile.models.responses.ResourcesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by rustie on 2/10/18.
 */

public interface BMAPI {

    @Headers("Cache-Control: max-age=640000")
    @GET("dining_halls")
    Call<DiningHallsResponse> callDiningHallList();

    @Headers("Cache-Control: max-age=640000")
    @GET("cafes")
    Call<CafesResponse> callCafeList();

    @Headers("Cache-Control: max-age=640000")
    @GET("weekly_libraries")
    Call<LibrariesResponse> callLibrariesList();

    @Headers("Cache-Control: max-age=640000")
    @GET("gyms")
    Call<GymsResponse> callGymsList();

    @Headers("Cache-Control: max-age=640000")
    @GET("group_exs")
    Call<GymClassesResponse> callGymClasses();

    @Headers("Cache-Control: max-age=640000")
    @GET("resources")
    Call<ResourcesResponse> callResourcesList();

}
