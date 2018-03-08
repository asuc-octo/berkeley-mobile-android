package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.models.GymClass;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by rustie on 3/6/18.
 */

public class GymClassesResponse {

    @SerializedName("CARDIO")
    ArrayList<GymClass> cardio;

    @SerializedName("ALL-AROUND WORKOUT")
    ArrayList<GymClass> allAround;

    @SerializedName("AQUA")
    ArrayList<GymClass> aqua;

    @SerializedName("STRENGTH")
    ArrayList<GymClass> strength;

    @SerializedName("DANCE")
    ArrayList<GymClass> dance;

    @SerializedName("MIND/BODY")
    ArrayList<GymClass> mindBody;

    @SerializedName("CORE")
    ArrayList<GymClass> core;

    public ArrayList<GymClass> getCardio() {
        return cardio;
    }

    public ArrayList<GymClass> getAllAround() {
        return allAround;
    }

    public ArrayList<GymClass> getAqua() {
        return aqua;
    }

    public ArrayList<GymClass> getStrength() {
        return strength;
    }

    public ArrayList<GymClass> getDance() {
        return dance;
    }

    public ArrayList<GymClass> getMindBody() {
        return mindBody;
    }

    public ArrayList<GymClass> getCore() {
        return core;
    }
}
