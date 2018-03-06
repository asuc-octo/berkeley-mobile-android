package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.models.FoodPlace;
import com.asuc.asucmobile.models.Library;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rustie on 2/19/18.
 */

public class LibrariesResponse {

    @SerializedName("libraries")
    private ArrayList<Library> libraries;

    public ArrayList<Library> getLibraries() {
        return libraries;
    }
}
