package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.domain.models.Library;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rustie on 2/19/18.
 */

public class GymsResponse {

    @SerializedName("gyms")
    private ArrayList<Gym> gyms;

    public ArrayList<Gym> getGyms() {
        return gyms;
    }

}
