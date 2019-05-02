package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.PTBusResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BusInfoResponse {

    @SerializedName("buses")
    private ArrayList<PTBusResponse> buses;

    public ArrayList<PTBusResponse> getBusInfos() {
        return buses;
    }

}

