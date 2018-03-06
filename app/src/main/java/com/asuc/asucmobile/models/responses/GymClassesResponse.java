package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.Gym;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by rustie on 3/6/18.
 */

public class GymClassesResponse {

    @SerializedName("CARDIO")
    ArrayList<Gym> cardio;

    @SerializedName("ALL-AROUND WORKOUT")
    ArrayList<Gym> allAround;

    @SerializedName("AQUA")
    ArrayList<Gym> aqua;

    @SerializedName("STRENGTH")
    ArrayList<Gym> strength;

    @SerializedName("DANCE")
    ArrayList<Gym> dance;

    @SerializedName("MIND/BODY")
    ArrayList<Gym> mindBody;

    @SerializedName("CORE")
    ArrayList<Gym> core;

    public ArrayList<Gym> getCardio() {
        return cardio;
    }

    public ArrayList<Gym> getAllAround() {
        return allAround;
    }

    public ArrayList<Gym> getAqua() {
        return aqua;
    }

    public ArrayList<Gym> getStrength() {
        return strength;
    }

    public ArrayList<Gym> getDance() {
        return dance;
    }

    public ArrayList<Gym> getMindBody() {
        return mindBody;
    }

    public ArrayList<Gym> getCore() {
        return core;
    }
}
