package com.asuc.asucmobile.dagger.components;

import com.asuc.asucmobile.dagger.modules.RepositoryModule;
import com.asuc.asucmobile.fragments.FoodFragment;
import com.asuc.asucmobile.infrastructure.DiningHallRepository;
import com.asuc.asucmobile.infrastructure.LibraryRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class})
public interface FirebaseComponent {

    void inject(DiningHallRepository repository);

    void inject(LibraryRepository repository);

}
