package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.CategoryLoc;
import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.domain.models.Resource;
import com.asuc.asucmobile.domain.repository.CategoryLocDomainRepository;
import com.asuc.asucmobile.domain.repository.MultiRepository;
import com.asuc.asucmobile.domain.services.BMService;
import com.asuc.asucmobile.infrastructure.CafeRetrofitRepository;
import com.asuc.asucmobile.infrastructure.DiningHallFirestoreRepository;
import com.asuc.asucmobile.infrastructure.FordGoBikeFirestoreRepository;
import com.asuc.asucmobile.infrastructure.GymRetrofitRepository;
import com.asuc.asucmobile.infrastructure.LibraryFirestoreRepository;
import com.asuc.asucmobile.infrastructure.MentalHealthFirestoreRepository;
import com.asuc.asucmobile.infrastructure.MicrowaveFirestoreRepository;
import com.asuc.asucmobile.infrastructure.NapPodFirestoreRepository;
import com.asuc.asucmobile.infrastructure.PrinterFirestoreRepository;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.infrastructure.ResourceFirestoreRepository;
import com.asuc.asucmobile.infrastructure.ResourceRetrofitRepository;
import com.asuc.asucmobile.infrastructure.WaterFountainFirestoreRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;

/**
 * Repository module for Firebase Firestore specifically
 */
@Module(includes = {FirebaseModule.class, RetrofitModule.class})
@NoArgsConstructor
public class RepositoryModule {

    //--------------------- Firestore Repositories -------------------------

    @Provides
    @Singleton
    public Repository<DiningHall> getDiningHallRepository(FirebaseFirestore firestore) {
        return new DiningHallFirestoreRepository(firestore);
    }

    @Provides
    @Singleton
    public Repository<Resource> getResourceRepository(FirebaseFirestore firestore) {
        return new ResourceFirestoreRepository(firestore);
    }

    @Provides
    @Singleton
    public Repository<Library> getLibraryRepository(FirebaseFirestore firestore) {
        return new LibraryFirestoreRepository(firestore);
    }

    // map icons thicc repository

    @Provides
    @Singleton
    public MultiRepository<String, CategoryLoc> getCategoryLocDomainRepository(FirebaseFirestore firestore) {
        return new CategoryLocDomainRepository(firestore);
    }

    //------------------------ Service Repositories ------------------------

//    @Provides
//    @Singleton
//    public Repository<DiningHall> getDiningHallRepository(BMService service) {
//        return new DiningHallRetrofitRepository(service);
//    }

    @Provides
    @Singleton
    public Repository<Cafe> getCafeRepository(BMService service) {
        return new CafeRetrofitRepository(service);
    }

//    @Provides
//    @Singleton
//    public Repository<Library> getLibraryRepository(BMService service) {
//        return new LibraryRetrofitRepository(service);
//    }

//    @Provides
//    @Singleton
//    public Repository<Resource> getResourceRepository(BMService service) {
//        return new ResourceRetrofitRepository(service);
//    }

    @Provides
    @Singleton
    public Repository<Gym> getGymRepository(BMService service) {
        return new GymRetrofitRepository(service);
    }
}
