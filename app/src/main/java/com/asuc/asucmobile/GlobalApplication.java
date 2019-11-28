package com.asuc.asucmobile;

import android.app.Application;
import android.content.Context;

import com.asuc.asucmobile.dagger.components.DaggerRepositoryComponent;
import com.asuc.asucmobile.dagger.components.RepositoryComponent;
import com.asuc.asucmobile.dagger.modules.FirebaseModule;
import com.asuc.asucmobile.dagger.modules.RepositoryModule;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.utilities.ServerUtils;
import com.pushwoosh.Pushwoosh;

/**
 * Configuration for the global application
 */
public class GlobalApplication extends Application {

    private static Context appContext;
    private static RepositoryComponent repositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        repositoryComponent = DaggerRepositoryComponent.builder()
                .repositoryModule(new RepositoryModule())
                .retrofitModule(new RetrofitModule(ServerUtils.getBaseUrl()))
                .firebaseModule(new FirebaseModule())
                .build();
    }

    public static RepositoryComponent getRepositoryComponent() {
        return repositoryComponent;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
