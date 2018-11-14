package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.domain.services.BMService;
import com.asuc.asucmobile.infrastructure.DiningHallFirestoreRepository;
import com.asuc.asucmobile.infrastructure.DiningHallRetrofitRepository;
import com.asuc.asucmobile.infrastructure.LibraryFirestoreRepository;
import com.asuc.asucmobile.infrastructure.LibraryRetrofitRepository;
import com.asuc.asucmobile.infrastructure.Repository;
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

//    @Provides
//    @Singleton
//    public Repository<DiningHall> getDiningHallRepository(FirebaseFirestore firestore) {
//        return new DiningHallFirestoreRepository(firestore);
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
    public Repository<Library> getLibraryRepository(BMService service) {
        return new LibraryRetrofitRepository(service);
    }


}
