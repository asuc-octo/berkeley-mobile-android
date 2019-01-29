package com.asuc.asucmobile.domain.models.responses;

import com.asuc.asucmobile.domain.models.LineRespModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LineResponse {
    @SerializedName("lines")
    private ArrayList<LineRespModel> lineRespModels;

    public ArrayList<LineRespModel> getLineRespModels() {
        return lineRespModels;
    }
}
