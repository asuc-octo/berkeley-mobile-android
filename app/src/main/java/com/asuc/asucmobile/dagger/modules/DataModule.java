package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.infrastructure.DiningHallFirestoreRepository;
import com.asuc.asucmobile.domain.models.DiningHall;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    public List<DiningHall> getDiningHalls(final DiningHallFirestoreRepository repository) {
        List<DiningHall> diningHalls = new ArrayList<>();
        repository.scanAll(diningHalls);
        return diningHalls;
    }
}
