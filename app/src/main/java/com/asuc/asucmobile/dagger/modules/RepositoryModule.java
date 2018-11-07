package com.asuc.asucmobile.dagger.modules;

import com.asuc.asucmobile.infrastructure.DiningHallFirestoreRepository;
import com.asuc.asucmobile.infrastructure.LibraryFirestoreRepository;
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
    public DiningHallFirestoreRepository getDiningHallRepository() {
        return new DiningHallFirestoreRepository();
    }

    @Provides
    @Singleton
    public LibraryFirestoreRepository getLibraryRepository() {
        return new LibraryFirestoreRepository();
    }
}
