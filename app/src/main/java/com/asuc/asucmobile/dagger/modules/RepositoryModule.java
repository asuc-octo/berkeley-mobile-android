package com.asuc.asucmobile.dagger.modules;

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

}
