package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.models.GroupExs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GroupExController {

    @GET("group_exs/")
    Call<GroupExs> getClasses();

}
