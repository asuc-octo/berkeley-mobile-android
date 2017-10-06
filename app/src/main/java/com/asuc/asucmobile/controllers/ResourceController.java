package com.asuc.asucmobile.controllers;

import android.content.Context;

import com.asuc.asucmobile.models.Resources;
import com.asuc.asucmobile.models.Resources.Resource;
import com.asuc.asucmobile.utilities.CustomComparators;

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
    public static List<Resource> parse(Resources resources, Context context) {
        for (Resource resource : resources.data) {
            parse(resource, context);
        }
        Collections.sort(resources.data, CustomComparators.FacilityComparators.getSortByFavoriteResource(context));
        return resources.data;
    }

    public static Resource parse(Resource resource, Context context) {
        resource.generateLatLng();
        return resource;
    }

    public static void setCurrentResource(Resource resource) {
        currentResource = resource;
    }

    public static Resource getCurrentResource() {
        return currentResource;
    }

}

