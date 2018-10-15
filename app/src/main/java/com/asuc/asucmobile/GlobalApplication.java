package com.asuc.asucmobile;

import android.app.Application;
import android.content.Context;

import com.asuc.asucmobile.dagger.components.DaggerRetrofitComponent;
import com.asuc.asucmobile.dagger.components.RetrofitComponent;
import com.asuc.asucmobile.dagger.modules.AppModule;
import com.asuc.asucmobile.dagger.modules.RetrofitModule;
import com.asuc.asucmobile.utilities.ServerUtils;

/**
 * Configuration for the global application
 */
public class GlobalApplication extends Application {

    private static Context appContext;
    private static RetrofitComponent retrofitComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        retrofitComponent = DaggerRetrofitComponent.builder()
                .appModule(new AppModule(this))
                .retrofitModule(new RetrofitModule(ServerUtils.getBaseUrl()))
                .build();

    }

    public static RetrofitComponent getRetrofitComponent() {
        return retrofitComponent;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
