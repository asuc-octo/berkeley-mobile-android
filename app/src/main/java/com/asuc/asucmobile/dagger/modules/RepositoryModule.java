package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.domain.models.Resource;
import com.asuc.asucmobile.domain.services.BMService;
import com.asuc.asucmobile.infrastructure.CafeRetrofitRepository;
import com.asuc.asucmobile.infrastructure.DiningHallRetrofitRepository;
import com.asuc.asucmobile.infrastructure.GymRetrofitRepository;
import com.asuc.asucmobile.infrastructure.LibraryFirestoreRepository;
import com.asuc.asucmobile.infrastructure.LibraryRetrofitRepository;
import com.asuc.asucmobile.infrastructure.Repository;
import com.asuc.asucmobile.infrastructure.ResourceFirestoreRepository;
import com.asuc.asucmobile.infrastructure.ResourceRetrofitRepository;
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

//    @Provides
//    @Singleton
//    public Repository<DiningHall> getDiningHallRepository(FirebaseFirestore firestore) {
//        return new DiningHallFirestoreRepository(firestore);
//    }
//
//    @Provides
//    @Singleton
//    public Repository<Resource> getResourceRepository(FirebaseFirestore firestore) {
//        return new ResourceFirestoreRepository(firestore);
//    }
//
//    @Provides
//    @Singleton
//    public Repository<Library> getLibraryRepository(FirebaseFirestore firestore) {
//        return new LibraryFirestoreRepository(firestore);
//    }

    //------------------------ Service Repositories ------------------------

    @Provides
    @Singleton
    public Repository<DiningHall> getDiningHallRepository(BMService service) {
        return new DiningHallRetrofitRepository(service);
    }

    @Provides
    @Singleton
    public Repository<Cafe> getCafeRepository(BMService service) {
        return new CafeRetrofitRepository(service);
    }

    @Provides
    @Singleton
    public Repository<Library> getLibraryRepository(BMService service) {
        return new LibraryRetrofitRepository(service);
    }

    @Provides
    @Singleton
    public Repository<Resource> getResourceRepository(BMService service) {
        return new ResourceRetrofitRepository(service);
    }

    @Provides
    @Singleton
    public Repository<Gym> getGymRepository(BMService service) {
        return new GymRetrofitRepository(service);
    }
}
