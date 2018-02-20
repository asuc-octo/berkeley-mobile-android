package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.models.Resource;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rustie on 2/19/18.
 */

public class ResourcesResponse {


    @SerializedName("resources")
    private ArrayList<Resource> resources;

    public ArrayList<Resource> getResources() {
        return resources;
    }

}
