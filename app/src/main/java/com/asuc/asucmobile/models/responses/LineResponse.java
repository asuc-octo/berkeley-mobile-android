package com.asuc.asucmobile.models.responses;

import com.asuc.asucmobile.models.LineRespModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LineResponse {
    @SerializedName("lines")
    private ArrayList<LineRespModel> lineRespModels;

    public ArrayList<LineRespModel> getLineRespModels() {
        return lineRespModels;
    }
}
