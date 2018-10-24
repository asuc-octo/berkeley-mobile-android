package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.infrastructure.DiningHallRepository;
import com.asuc.asucmobile.models.DiningHall;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    public List<DiningHall> getDiningHalls(final DiningHallRepository repository) {
        List<DiningHall> diningHalls = new ArrayList<>();
        repository.scanAllDiningHalls(diningHalls);
        return diningHalls;
    }
}
