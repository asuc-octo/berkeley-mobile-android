package com.asuc.asucmobile.tests;

import com.asuc.asucmobile.controllers.BMAPI;
import com.asuc.asucmobile.models.DiningHall;
import com.asuc.asucmobile.utilities.ServiceGenerator;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by rustie on 2/10/18.
 */

public class BMAPITest {

    public static void main(String[] args) throws IOException {
        BMAPI bmapi = ServiceGenerator.createService(BMAPI.class);
        Call<List<DiningHall>> call = bmapi.diningHallList();
        List<DiningHall> diningHalls = call.execute().body();
    }
}
