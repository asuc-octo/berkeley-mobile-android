package com.asuc.asucmobile.dagger.components;

import com.asuc.asucmobile.dagger.modules.AppModule;
import com.asuc.asucmobile.dagger.modules.DataModule;
import com.asuc.asucmobile.dagger.modules.RepositoryModule;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.domain.fragments.FoodFragment;
import com.asuc.asucmobile.domain.fragments.GymFragment;
import com.asuc.asucmobile.domain.fragments.LibraryFragment;
import com.asuc.asucmobile.domain.fragments.MapsFragment;
import com.asuc.asucmobile.domain.fragments.ResourceFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RetrofitModule.class, RepositoryModule.class, DataModule.class, AppModule.class})
public interface DataComponent {

    void inject(FoodFragment fragment);

    void inject(LibraryFragment fragment);

    void inject(GymFragment fragment);

    void inject(ResourceFragment fragment);

    void inject(MapsFragment fragment);

}
