package com.asuc.asucmobile.dagger.components;

import com.asuc.asucmobile.dagger.modules.AppModule;
import com.asuc.asucmobile.dagger.modules.RepositoryModule;
import com.asuc.asucmobile.domain.fragments.FoodFragment;
import com.asuc.asucmobile.domain.fragments.GymFragment;
import com.asuc.asucmobile.domain.fragments.LibraryFragment;
import com.asuc.asucmobile.domain.fragments.MapsFragment;
import com.asuc.asucmobile.domain.fragments.ResourceFragment;
import com.asuc.asucmobile.domain.models.Resource;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class, AppModule.class})
public interface RepositoryComponent {

    void inject(FoodFragment foodFragment);

    void inject(LibraryFragment libraryFragment);

    void inject(GymFragment gymFragment);

    void inject(ResourceFragment resourceFragment);

    void inject(MapsFragment mapsFragment);

}
