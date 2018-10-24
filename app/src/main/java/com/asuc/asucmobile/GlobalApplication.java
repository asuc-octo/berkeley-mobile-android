package com.asuc.asucmobile;

import android.app.Application;
import android.content.Context;

import com.asuc.asucmobile.dagger.components.DaggerDataComponent;
import com.asuc.asucmobile.dagger.components.DaggerFirebaseComponent;
import com.asuc.asucmobile.dagger.components.DaggerRetrofitComponent;
import com.asuc.asucmobile.dagger.components.DataComponent;
import com.asuc.asucmobile.dagger.components.FirebaseComponent;
import com.asuc.asucmobile.dagger.components.RetrofitComponent;
import com.asuc.asucmobile.dagger.modules.AppModule;
import com.asuc.asucmobile.dagger.modules.DataModule;
import com.asuc.asucmobile.dagger.modules.RepositoryModule;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.utilities.ServerUtils;

/**
 * Configuration for the global application
 */
public class GlobalApplication extends Application {

    private static Context appContext;
    private static RetrofitComponent retrofitComponent;
    private static FirebaseComponent firebaseComponent;
    private static DataComponent dataComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        dataComponent = DaggerDataComponent.builder()
                .retrofitModule(new RetrofitModule(ServerUtils.getBaseUrl()))
                .dataModule(new DataModule())
                .appModule(new AppModule(this))
                .repositoryModule(new RepositoryModule())
                .build();

        firebaseComponent = DaggerFirebaseComponent.builder()
                .repositoryModule(new RepositoryModule())
                .build();

        retrofitComponent = DaggerRetrofitComponent.builder()
                .appModule(new AppModule(this))
                .retrofitModule(new RetrofitModule(ServerUtils.getBaseUrl()))
                .build();

    }

    public static DataComponent getDataComponent() {
        return dataComponent;
    }

    public static FirebaseComponent getFirebaseComponent () {
        return firebaseComponent;
    }

    public static RetrofitComponent getRetrofitComponent() {
        return retrofitComponent;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
