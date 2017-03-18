package com.asuc.asucmobile.controllers;

import com.asuc.asucmobile.models.GroupExs;
import com.asuc.asucmobile.models.GroupExs.GroupEx;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class GroupExController {
    public interface cService {

        @GET("group_exs/")
        Call<GroupExs> getClasses();

    }

    public List<GroupEx> parse(GroupExs groupExs) {
        return groupExs.groupExs;
    }
}
