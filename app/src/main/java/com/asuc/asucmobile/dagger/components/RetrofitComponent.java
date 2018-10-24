package com.asuc.asucmobile.dagger.components;

import com.asuc.asucmobile.services.BMService;
import com.asuc.asucmobile.dagger.modules.AppModule;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.fragments.FoodFragment;
import com.asuc.asucmobile.fragments.GymFragment;
import com.asuc.asucmobile.fragments.LibraryFragment;
import com.asuc.asucmobile.fragments.ResourceFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RetrofitModule.class, AppModule.class})
public interface RetrofitComponent {

    // TODO: try a more generalized injection function.
    // Update! Check out DataComponent.java

//    void inject(FoodFragment fragment);
//
//    void inject(LibraryFragment fragment);
//
//    void inject(GymFragment fragment);
//
//    void inject(ResourceFragment fragment);

    // testing?

    BMService bmapi();

}
