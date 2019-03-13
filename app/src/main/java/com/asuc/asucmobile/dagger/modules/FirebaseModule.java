package com.asuc.asucmobile.dagger.modules;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.NoArgsConstructor;

@Module
@NoArgsConstructor
public class FirebaseModule {

    @Provides
    @Singleton
    public FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }
}
