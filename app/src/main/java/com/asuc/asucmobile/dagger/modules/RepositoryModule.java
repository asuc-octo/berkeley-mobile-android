package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.infrastructure.DiningHallRepository;
import com.asuc.asucmobile.infrastructure.LibraryRepository;
import com.asuc.asucmobile.infrastructure.transformers.DiningHallTransformer;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    // singleton provider for Firestore instance
    @Provides
    @Singleton
    public FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public DiningHallRepository getDiningHallRepository() {
        return new DiningHallRepository();
    }

    @Provides
    @Singleton
    public LibraryRepository getLibraryRepository() {
        return new LibraryRepository();
    }
}
