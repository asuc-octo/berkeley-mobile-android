package com.asuc.asucmobile.infrastructure;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.infrastructure.transformers.DiningHallTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class DiningHallTestRepository implements Repository<DiningHall> {


    protected List<DiningHall> diningHalls;

    /**
     * Mock a DiningHall repo
     */
    public DiningHallTestRepository() {
        Gson gson = new Gson();
        DiningHallTransformer transformer = new DiningHallTransformer();
        try {
            Type listInfra = new TypeToken<List<com.asuc.asucmobile.infrastructure.models.DiningHall>>() {}.getType();
            List<com.asuc.asucmobile.infrastructure.models.DiningHall> infraDiningHalls = gson.fromJson(new FileReader("data/dining_halls.json"), listInfra);
            diningHalls = transformer.diningHallListInfraDomainTransformer(infraDiningHalls);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
        }
    }

    @Override
    public List<DiningHall> scanAll(List<DiningHall> list) {
        return diningHalls;
    }
}
