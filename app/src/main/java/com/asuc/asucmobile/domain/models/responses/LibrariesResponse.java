package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.FoodPlace;
import com.asuc.asucmobile.domain.models.Library;
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
