package com.asuc.asucmobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asuc.asucmobile.models.Resources;
import com.asuc.asucmobile.models.Resources.Resource;
import com.asuc.asucmobile.utilities.Callback;
import com.asuc.asucmobile.utilities.CustomComparators;
import com.asuc.asucmobile.utilities.JSONUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class ResourceController {

    private static Resource currentResource;

    public interface cService {
        @GET("resources")
        Call<Resources> getResources();
    }
    public static List<Resource> parse(Resources resources) {
        return resources.resources;
    }

    public static void setCurrentResource(Resource resource) {
        currentResource = resource;
    }

    public static Resource getCurrentResource() {
        return currentResource;
    }

}

