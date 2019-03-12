package com.asuc.asucmobile.infrastructure.transformers;


import com.asuc.asucmobile.domain.models.BusDeparture;
import com.asuc.asucmobile.domain.models.BusInfo;
import com.asuc.asucmobile.domain.models.PTBusResponse;

import java.util.ArrayList;

public class BusInfoTransformer {
//    private ArrayList<PTBusResponse> ptbuses;
//    private ArrayList<BusInfo> businfos;

    public static ArrayList<BusInfo> getTransformedBusInfos(ArrayList<PTBusResponse> inputBuses, String title) {
        ArrayList<BusInfo> busInfos = new ArrayList<>();

        for (PTBusResponse busResponse : inputBuses) {
            busInfos.add(new BusInfo(title, (ArrayList<BusDeparture>) busResponse.getValues(), busResponse.getRoute().getTitle(), ((ArrayList<BusDeparture>) busResponse.getValues()).get(0).getBusRouteName()));
        }

        return busInfos;
    }

}
